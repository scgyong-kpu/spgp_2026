package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy

import android.graphics.Path
import android.graphics.Rect
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.SheetSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main.MainScene
import kotlin.random.Random

class Fly private constructor(gctx: GameContext):
    SheetSprite(gctx, R.mipmap.galaga_flies, 2f), IRecyclable
{
    enum class Type(
        val health: Float,
    ) {
        BOSS(150f),
        RED(50f),
        BLUE(30f),
        CYAN(20f),
        DRAGON(10f),

        ;

        companion object {
            fun random(): Type {
                val value = Random.nextInt(100)

                // 0~9: RED 10%, 10~29: BLUE 20%, 30~59: CYAN 30%, 60~99: DRAGON 40%.
                // BOSS 는 일반 wave 에서 랜덤 생성하지 않고, 나중에 boss wave 전용 규칙으로 다룬다.
                return when {
                    value < 10 -> RED
                    value < 30 -> BLUE
                    value < 60 -> CYAN
                    else -> DRAGON
                }
            }
        }
    }

    init {
        setSize(MIN_SIZE, MIN_SIZE)
    }

    var life = 0f
        private set
    var maxLife = 0f
        private set
    private var speed = MIN_SPEED

    private fun init(type: Type, sizeRatio: Float): Fly {
        frameRects = rectsArray[type.ordinal]
        life = type.health
        maxLife = life
        speed = Random.nextFloat() * (MAX_SPEED - MIN_SPEED) + MIN_SPEED
        val size = (Random.nextFloat() * (MAX_SIZE - MIN_SIZE) + MIN_SIZE) * sizeRatio
        setSize(size, size)
        return this
    }

    override fun update(gctx: GameContext) {
        x += speed * gctx.frameTime
        setCenter(x, y)
        if (x - width / 2f > gctx.metrics.width) {
            (gctx.scene as MainScene).world.remove(this, MainScene.Layer.ENEMY)
        }
    }

    override fun onRecycle() {}

    companion object {
        fun get(gctx: GameContext): Fly {
            return obtain(gctx, Type.random())
        }

        fun boss(gctx: GameContext): Fly {
            return obtain(gctx, Type.BOSS, sizeRatio = BOSS_SIZE_SCALE)
        }

        private fun obtain(gctx: GameContext, type: Type, sizeRatio: Float = 1.0f): Fly {
            val world = (gctx.scene as MainScene).world
            val fly = world.obtain(Fly::class.java) ?: Fly(gctx)
            return fly.init(type, sizeRatio)
        }

        // galaga_flies.png 는 700x70 고정 이미지이고, type 별로 70x70 frame 이 2장씩 이어져 있다.
        // 이미지 규격이 바뀔 일이 없는 asset 이므로 런타임 계산 대신 상수 Rect 목록으로 둔다.
        private val rectsArray = listOf(
            listOf(Rect(0, 0, 70, 70), Rect(70, 0, 140, 70)),
            listOf(Rect(140, 0, 210, 70), Rect(210, 0, 280, 70)),
            listOf(Rect(280, 0, 350, 70), Rect(350, 0, 420, 70)),
            listOf(Rect(420, 0, 490, 70), Rect(490, 0, 560, 70)),
            listOf(Rect(560, 0, 630, 70), Rect(630, 0, 700, 70)),
        )

        val path = Path().apply {
            moveTo(0f, 900f)
            lineTo(350f, 0f)
            lineTo(800f, 900f)
            lineTo(1250f, 0f)
            lineTo(1600f, 900f)
        }

        private const val MIN_SIZE = 75f
        private const val MAX_SIZE = 125f
        private const val BOSS_SIZE_SCALE = 1.5f
        private const val MIN_SPEED = 25f
        private const val MAX_SPEED = 60f
    }
}
