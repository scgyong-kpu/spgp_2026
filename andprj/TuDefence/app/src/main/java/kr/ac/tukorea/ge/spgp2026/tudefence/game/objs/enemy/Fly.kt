package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy

import android.graphics.Canvas
import android.graphics.PathMeasure
import android.graphics.Rect
import androidx.core.graphics.PathParser
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.SheetSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Gauge
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.common.IRadiusCollidable
import kr.ac.tukorea.ge.spgp2026.tudefence.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.tudefence.game.layer.mainWorld
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.random.Random

class Fly private constructor(gctx: GameContext):
    SheetSprite(gctx, R.mipmap.galaga_flies, 2f), IRecyclable, IRadiusCollidable
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

    override val radius: Float
        get() = width / 2f

    var type = Type.DRAGON
    var distance = 0f
    var life = 0f
        private set
    var maxLife = 0f
        private set
    private var displayLife = 0f
    private var speed = MIN_SPEED
    private var angle = 0f
    private var pathOffset = 0f
    private val position = FloatArray(2)
    private val tangent = FloatArray(2)

    private fun init(type: Type, sizeRatio: Float): Fly {
        this.type = type
        frameRects = rectsArray[type.ordinal]
        life = type.health
        maxLife = life
        displayLife = life
        speed = Random.nextFloat() * (MAX_SPEED - MIN_SPEED) + MIN_SPEED
        val size = (Random.nextFloat() * (MAX_SIZE - MIN_SIZE) + MIN_SIZE) * sizeRatio
        setSize(size, size)
        distance = 0f
        val maxOffset = width / 5f
        pathOffset = (Random.nextFloat() * 2f - 1f) * maxOffset
        updatePosition()
        return this
    }

    override fun update(gctx: GameContext) {
        updateDisplayLife()
        distance += speed * gctx.frameTime
        updateOffset(gctx)
        updatePosition()
        if (distance > pathLength) {
            gctx.mainWorld().remove(this, MainLayer.ENEMY)
        }
    }

    private fun updateDisplayLife() {
        if (life == displayLife) return

        val step = maxLife / LIFE_GAUGE_ANIMATION_STEP_COUNT
        val diff = life - displayLife
        displayLife += when {
            diff < -step -> -step
            diff > step -> step
            else -> diff
        }
    }

    private fun updateOffset(gctx: GameContext) {
        val maxOffset = width / 5f
        val offsetDelta = (Random.nextFloat() * 2f - 1f) * maxOffset * gctx.frameTime
        pathOffset = (pathOffset + offsetDelta).coerceIn(-maxOffset, maxOffset)
    }

    private fun updatePosition() {
        pathMeasure.getPosTan(distance, position, tangent)
        val tangentLength = hypot(tangent[0], tangent[1])
        val normalX = -tangent[1] / tangentLength
        val normalY = tangent[0] / tangentLength
        setCenter(position[0] + normalX * pathOffset, position[1] + normalY * pathOffset)
        angle = Math.toDegrees(atan2(tangent[1], tangent[0]).toDouble()).toFloat()
    }

    fun decreaseLife(amount: Float) {
        life -= amount
    }

    fun isDead(): Boolean {
        return life <= 0f
    }

    fun score(): Int {
        // 현재 Fly 는 health 를 10 단위로 정의하고 있으므로,
        // 죽었을 때 주는 점수도 health 크기와 같은 흐름으로 맞춘다.
        // 나중에 level/종류별 보상 규칙이 더 세분화되면 여기만 바꾸면 된다.
        return maxLife.toInt()
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

        // Gauge 는 색/두께만 가진 stateless drawing helper 이다.
        // Fly 마다 Gauge 를 만들면 적이 생성될 때마다 Paint 객체도 같이 생기므로,
        // companion object 의 lifeGauge 하나를 모든 Fly 가 공유하고 progress 만 넘긴다.
        val barSize = width * LIFE_GAUGE_WIDTH_RATIO
        lifeGauge.draw(
            canvas,
            x - barSize / 2f,
            y + barSize / 2f,
            barSize,
            displayLife / maxLife,
        )
    }

    override fun onRecycle() {}

    override fun toString(): String {
        return "Fly($type/${x.toInt()},${y.toInt()})"
    }
    companion object {
        fun get(gctx: GameContext): Fly {
            return obtain(gctx, Type.random())
        }

        fun boss(gctx: GameContext): Fly {
            return obtain(gctx, Type.BOSS, sizeRatio = BOSS_SIZE_SCALE)
        }

        private fun obtain(gctx: GameContext, type: Type, sizeRatio: Float = 1.0f): Fly {
            val world = gctx.mainWorld()
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
            "M -72,896\n" +
            "C 64,896 192,792 192,656\n" +
            "C 192,520 48,472 48,336\n" +
            "C 40,192 152,40 320,32\n" +
            "C 480,32 600,184 600,328\n" +
            "C 600,472 488,488 496,632\n" +
            "C 496,776 712,848 800,848\n" +
            "C 888,848 1104,776 1104,632\n" +
            "C 1112,488 1000,472 1000,328\n" +
            "C 1000,184 1120,32 1280,32\n" +
            "C 1448,40 1560,192 1552,336\n" +
            "C 1552,472 1408,520 1408,656\n" +
            "C 1408,792 1536,896 1672,896\n"
        )!!

        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length

        private const val MIN_SIZE = 75f
        private const val MAX_SIZE = 125f
        private const val BOSS_SIZE_SCALE = 1.5f
        private const val MIN_SPEED = 25f
        private const val MAX_SPEED = 60f
        private const val LIFE_GAUGE_THICKNESS = 0.2f
        private const val LIFE_GAUGE_WIDTH_RATIO = 2f / 3f
        private const val LIFE_GAUGE_ANIMATION_STEP_COUNT = 50f
        private val lifeGauge = Gauge(
            thickness = LIFE_GAUGE_THICKNESS,
            fgColor = "#C9786400".toColorInt(),
            bgColor = "#B5FFD7D5".toColorInt(),
        )
    }
}
