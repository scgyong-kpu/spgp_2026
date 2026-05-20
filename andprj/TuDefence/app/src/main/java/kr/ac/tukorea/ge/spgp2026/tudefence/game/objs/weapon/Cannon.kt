package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.weapon

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.tudefence.game.layer.mainWorld
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy.Fly
import kotlin.math.atan2
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.roundToInt

class Cannon private constructor(private val gctx: GameContext): Sprite(gctx, R.mipmap.cannon_bg_1_256) {
    private val barrelRect = RectF()
    private val barrelBitmap = gctx.res.getBitmap(R.mipmap.barrel_512)
    private var currentLevel = 1
        set(value) {
            // Cannon level 은 학생들이 보기 쉽게 1-based index 로 사용한다.
            // setter 안에서 this.currentLevel = ... 을 다시 쓰면 setter 가 재귀 호출된다.
            // 그래서 Kotlin property setter 에서는 backing field 인 field 에 직접 대입한다.
            field = value.coerceIn(MIN_LEVEL, MAX_LEVEL)
            val levelRatio = (field - MIN_LEVEL) / (MAX_LEVEL - MIN_LEVEL).toFloat()
            barrelSize = MIN_BARREL_SIZE + (MAX_BARREL_SIZE - MIN_BARREL_SIZE) * levelRatio
            fireInterval = MAX_FIRE_INTERVAL - FIRE_INTERVAL_STEP * (field - MIN_LEVEL)
            range = BASE_RANGE + RANGE_PER_LEVEL * field
            bitmap = gctx.res.getBitmap(if (field >= UPGRADED_IMAGE_MIN_LEVEL) {
                R.mipmap.cannon_bg_6_256
            } else {
                R.mipmap.cannon_bg_1_256
            })
        }
    private var barrelSize = MIN_BARREL_SIZE
    private var fireInterval = MAX_FIRE_INTERVAL
    private var range = MIN_RANGE
    private var angle = 0f
    private var fireCooldown = 0f

    val level: Int
        get() = currentLevel

    init {
        setSize(BASE_SIZE, BASE_SIZE)
    }

    private fun init(level: Int): Cannon {
        this.currentLevel = level
        fireCooldown = fireInterval
        return this
    }

    override fun update(gctx: GameContext) {
        fireCooldown -= gctx.frameTime
        val target = findNearestEnemy(gctx) ?: return
        angle = Math.toDegrees(atan2((target.y - y).toDouble(), (target.x - x).toDouble())).toFloat()
        if (fireCooldown <= 0f) {
            fire(gctx)
            fireCooldown = fireInterval
        }
    }

    private fun findNearestEnemy(gctx: GameContext): Fly? {
        val world = gctx.mainWorld()
        val enemies = world.objectsAt(MainLayer.ENEMY)
        var nearest: Fly? = null
        var nearestDistanceSq = range * range

        // update() 에서 매 프레임 도는 탐색이므로 for-each 대신 index 기반 while 을 쓴다.
        // Cannon 은 level 별 사거리 안에 들어온 Fly 만 바라보고 공격한다.
        // 실제 거리를 구하려고 sqrt() 를 호출하지 않고 거리 제곱끼리 비교하면
        // 매 프레임 반복되는 target 탐색에서 불필요한 계산을 줄일 수 있다.
        var index = 0
        while (index < enemies.size) {
            val enemy = enemies[index] as? Fly
            if (enemy != null) {
                val dx = enemy.x - x
                val dxSq = dx * dx
                if (dxSq > nearestDistanceSq) {
                    index++
                    continue
                }

                val dy = enemy.y - y
                val dySq = dy * dy
                if (dySq > nearestDistanceSq) {
                    index++
                    continue
                }

                val distanceSq = dxSq + dySq
                if (distanceSq < nearestDistanceSq) {
                    nearestDistanceSq = distanceSq
                    nearest = enemy
                }
            }
            index++
        }
        return nearest
    }

    private fun fire(gctx: GameContext) {
        val radians = Math.toRadians(angle.toDouble())
        val muzzleOffset = barrelSize * BARREL_MUZZLE_OFFSET_RATIO
        val startX = x + cos(radians).toFloat() * muzzleOffset
        val startY = y + sin(radians).toFloat() * muzzleOffset
        val shell = Shell.get(gctx, startX, startY, angle, level)
        gctx.mainWorld().add(shell, MainLayer.SHELL)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // 포탑 몸체는 Sprite 의 dstRect 에 맞춰 그리고,
        // 포신은 같은 중심을 기준으로 별도 bitmap 을 얹는다.
        // barrel 이미지는 오른쪽을 보는 상태로 만들어 두고, 현재 angle 만큼 회전해 적을 바라보게 한다.
        barrelRect.set(
            x - barrelSize / 2f,
            y - barrelSize / 2f,
            x + barrelSize / 2f,
            y + barrelSize / 2f,
        )
        canvas.withRotation(angle, x, y) {
            canvas.drawBitmap(barrelBitmap, null, barrelRect, null)
        }
    }

    fun drawRange(canvas: Canvas) {
        canvas.drawCircle(x, y, range, rangePaint)
    }

    fun upgrade(): Boolean {
        // 메뉴에서 upgrade 를 눌렀을 때 호출하는 진입점이다.
        // level setter 가 사거리, 발사 간격, barrel 크기, body bitmap 을 함께 갱신한다.
        if (currentLevel >= MAX_LEVEL) return false
        currentLevel++
        return true
    }

    fun uninstall() {
        // Cannon 는 자기 자신이 어느 layer 에 들어있는지 알고 있지 않으므로,
        // GameContext 를 통해 현재 main world 에서 WEAPON layer 로부터 제거한다.
        // remove() 가 끝나면 world 는 이 Cannon 을 더 이상 update/draw 하지 않는다.
        gctx.mainWorld().remove(this, MainLayer.WEAPON)
    }

    fun intersectsIfInstalledAt(x: Float, y: Float): Boolean {
        // 설치 위치는 MainScene 에서 tile 중심으로 snap 된 값만 들어온다.
        // 새 Cannon 과 기존 Cannon 의 중심 차이가 x/y 양쪽 모두 BASE_SIZE 보다 작으면
        // 두 100x100 설치 영역이 서로 겹친다고 판단할 수 있다.
        return abs(this.x - x) < BASE_SIZE && abs(this.y - y) < BASE_SIZE
    }

    companion object {
        fun get(gctx: GameContext, level: Int): Cannon {
            return Cannon(gctx).init(level)
        }

        fun installationCost(toLevel: Int): Int {
            return COSTS[toLevel - 1]
        }

        fun upgradeCost(fromLevel: Int): Int {
            if (fromLevel >= MAX_LEVEL) return Int.MAX_VALUE
            return ((COSTS[fromLevel] - COSTS[fromLevel - 1]) * 1.1f).roundToInt()
        }

        private const val MIN_LEVEL = 1
        private const val MAX_LEVEL = 10
        private const val UPGRADED_IMAGE_MIN_LEVEL = 6
        private val COSTS = intArrayOf(
            10, 100, 300, 700, 1500, 3000, 7000, 15000, 40000, 100000, 100000000
        )
        const val SIZE = 100f
        private const val BASE_SIZE = SIZE
        private const val MIN_BARREL_SIZE = 110f
        private const val MAX_BARREL_SIZE = 200f
        private const val BARREL_MUZZLE_OFFSET_RATIO = 0.38f
        private const val MAX_FIRE_INTERVAL = 5.0f
        private const val FIRE_INTERVAL_STEP = 0.4f
        private const val BASE_RANGE = 100f
        private const val RANGE_PER_LEVEL = 100f
        private const val MIN_RANGE = BASE_RANGE + RANGE_PER_LEVEL * MIN_LEVEL
        private val rangePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 10f
            pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            color = "#7F7F0000".toColorInt()
        }
    }
}
