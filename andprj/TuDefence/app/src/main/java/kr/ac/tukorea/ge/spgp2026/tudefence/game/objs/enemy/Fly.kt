package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy

import android.graphics.Canvas
import android.graphics.PathMeasure
import android.graphics.Rect
import androidx.core.graphics.PathParser
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.SheetSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main.MainScene
import kotlin.math.atan2
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

    var distance = 0f
    var life = 0f
        private set
    var maxLife = 0f
        private set
    private var speed = MIN_SPEED
    private var angle = 0f
    private val position = FloatArray(2)
    private val tangent = FloatArray(2)

    private fun init(type: Type, sizeRatio: Float): Fly {
        frameRects = rectsArray[type.ordinal]
        life = type.health
        maxLife = life
        speed = Random.nextFloat() * (MAX_SPEED - MIN_SPEED) + MIN_SPEED
        val size = (Random.nextFloat() * (MAX_SIZE - MIN_SIZE) + MIN_SIZE) * sizeRatio
        setSize(size, size)
        distance = 0f
        updatePosition()
        return this
    }

    override fun update(gctx: GameContext) {
        distance += speed * gctx.frameTime
        updatePosition()
        if (distance > pathLength) {
            (gctx.scene as MainScene).world.remove(this, MainScene.Layer.ENEMY)
        }
    }

    private fun updatePosition() {
        pathMeasure.getPosTan(distance, position, tangent)
        setCenter(position[0], position[1])
        angle = Math.toDegrees(atan2(tangent[1], tangent[0]).toDouble()).toFloat()
    }

    override fun draw(canvas: Canvas) {
        // withRotation 은 아래 save/rotate/restore 패턴을 보기 좋게 감싼 AndroidX KTX helper 이다.
        // canvas.save()
        // canvas.rotate(angle, x, y)
        // super.draw(canvas)
        // canvas.restore()
        canvas.withRotation(angle, x, y) {
            super.draw(canvas)
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

        val path = PathParser.createPathFromPathData(
            "M -64,908.8\n" +
            "C 99.2,892.8 192,779.2 192,640\n" +
            "C 192,500.8 43.2,478.4 44.8,328\n" +
            "C 46.4,177.6 166.4,27.2 320,25.6\n" +
            "C 473.6,24 588.8,169.6 592,324.8\n" +
            "C 595.2,480 494.4,462.4 496,625.6\n" +
            "C 497.6,788.8 720,846.4 804.8,846.4\n" +
            "C 889.6,846.4 1108.8,758.4 1110.4,632\n" +
            "C 1112,505.6 996.8,470.4 996.8,331.2\n" +
            "C 996.8,192 1118.4,41.6 1288,43.2\n" +
            "C 1457.6,44.8 1560,209.6 1555.2,337.6\n" +
            "C 1550.4,465.6 1408,539.2 1409.6,670.4\n" +
            "C 1411.2,801.6 1577.6,891.2 1680,883.2\n"
        )!!

        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length

        private const val MIN_SIZE = 75f
        private const val MAX_SIZE = 125f
        private const val BOSS_SIZE_SCALE = 1.5f
        private const val MIN_SPEED = 25f
        private const val MAX_SPEED = 60f
    }
}
