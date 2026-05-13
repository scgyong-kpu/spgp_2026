package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy

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
        val spawnRate: Int,
    ) {
        BOSS(150f, 0),
        RED(50f, 10),
        BLUE(30f, 20),
        CYAN(20f, 30),
        DRAGON(10f, 40),

        ;

        companion object {
            private val totalSpawnRate = entries.sumOf { it.spawnRate }

            fun random(): Type {
                var selectedRate = Random.nextInt(totalSpawnRate)

                // Type 이 자기 spawnRate 규칙을 직접 가진다.
                // spawnRate 를 차례로 빼다가 음수가 되는 지점이 선택된 type 이다.
                // spawnRate 가 0 인 BOSS 는 기본 랜덤 생성에서는 선택되지 않는다.
                // for-in 은 iterator 객체 생성 가능성이 있으므로, 게임 중 자주 불릴 수 있는 곳에서는 index loop 를 쓴다.
                for (i in 0 ..< entries.size) {
                    val type = entries[i]
                    selectedRate -= type.spawnRate
                    if (selectedRate < 0) {
                        return type
                    }
                }

                return DRAGON
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

    private fun init(type: Type): Fly {
        frameRects = rectsArray[type.ordinal]
        life = type.health
        maxLife = life
        speed = Random.nextFloat() * (MAX_SPEED - MIN_SPEED) + MIN_SPEED
        val size = Random.nextFloat() * (MAX_SIZE - MIN_SIZE) + MIN_SIZE
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
            val world = (gctx.scene as MainScene).world
            val fly = world.obtain(Fly::class.java) ?: Fly(gctx)
            return fly.init(Type.random())
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

        private const val MIN_SIZE = 75f
        private const val MAX_SIZE = 125f
        private const val MIN_SPEED = 25f
        private const val MAX_SPEED = 60f
    }
}
