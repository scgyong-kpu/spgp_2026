package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.weapon

import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy.Fly
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main.MainScene
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Cannon private constructor(gctx: GameContext): Sprite(gctx, R.mipmap.cannon_bg_256) {
    private val barrelRect = RectF()
    private val barrelBitmap = gctx.res.getBitmap(R.mipmap.barrel_512)
    private var level = 1
    private var barrelSize = MIN_BARREL_SIZE
    private var fireInterval = MAX_FIRE_INTERVAL
    private var range = MIN_RANGE
    private var angle = 0f
    private var fireCooldown = 0f

    init {
        setSize(BASE_SIZE, BASE_SIZE)
    }

    private fun init(level: Int): Cannon {
        // Cannon level 은 학생들이 보기 쉽게 1-based index 로 사용한다.
        // level 1 은 가장 작은 포신, level 10 은 가장 큰 포신이며,
        // 범위를 벗어난 값은 안전하게 1..10 안으로 보정한다.
        this.level = level.coerceIn(MIN_LEVEL, MAX_LEVEL)
        val levelRatio = (this.level - MIN_LEVEL) / (MAX_LEVEL - MIN_LEVEL).toFloat()
        barrelSize = MIN_BARREL_SIZE + (MAX_BARREL_SIZE - MIN_BARREL_SIZE) * levelRatio
        fireInterval = MAX_FIRE_INTERVAL - FIRE_INTERVAL_STEP * (this.level - MIN_LEVEL)
        range = BASE_RANGE + RANGE_PER_LEVEL * this.level
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
        val world = (gctx.scene as MainScene).world
        val enemies = world.objectsAt(MainScene.Layer.ENEMY)
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
        (gctx.scene as MainScene).world.add(shell, MainScene.Layer.SHELL)
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

    companion object {
        fun get(gctx: GameContext, level: Int): Cannon {
            return Cannon(gctx).init(level)
        }

        private const val MIN_LEVEL = 1
        private const val MAX_LEVEL = 10
        private const val BASE_SIZE = 100f
        private const val MIN_BARREL_SIZE = 110f
        private const val MAX_BARREL_SIZE = 200f
        private const val BARREL_MUZZLE_OFFSET_RATIO = 0.38f
        private const val MAX_FIRE_INTERVAL = 5.0f
        private const val FIRE_INTERVAL_STEP = 0.4f
        private const val BASE_RANGE = 200f
        private const val RANGE_PER_LEVEL = 200f
        private const val MIN_RANGE = BASE_RANGE + RANGE_PER_LEVEL * MIN_LEVEL
    }
}
