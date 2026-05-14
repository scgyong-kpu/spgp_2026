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

class Cannon(gctx: GameContext): Sprite(gctx, R.mipmap.cannon_bg_256) {
    private val barrelRect = RectF()
    private val barrelBitmap = gctx.res.getBitmap(R.mipmap.barrel_512)
    private var angle = 0f

    init {
        setSize(BASE_SIZE, BASE_SIZE)
    }

    override fun update(gctx: GameContext) {
        val target = findNearestEnemy(gctx) ?: return
        angle = Math.toDegrees(atan2((target.y - y).toDouble(), (target.x - x).toDouble())).toFloat()
    }

    private fun findNearestEnemy(gctx: GameContext): Fly? {
        val world = (gctx.scene as MainScene).world
        val enemies = world.objectsAt(MainScene.Layer.ENEMY)
        var nearest: Fly? = null
        var nearestDistanceSq = Float.MAX_VALUE

        // update() 에서 매 프레임 도는 탐색이므로 for-each 대신 index 기반 while 을 쓴다.
        // 지금은 사거리 제한 없이 가장 가까운 Fly 를 바라보는 단계이다.
        var index = 0
        while (index < enemies.size) {
            val enemy = enemies[index] as? Fly
            if (enemy != null) {
                val dx = enemy.x - x
                val dy = enemy.y - y
                val distanceSq = dx * dx + dy * dy
                if (distanceSq < nearestDistanceSq) {
                    nearestDistanceSq = distanceSq
                    nearest = enemy
                }
            }
            index++
        }
        return nearest
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // 포탑 몸체는 Sprite 의 dstRect 에 맞춰 그리고,
        // 포신은 같은 중심을 기준으로 별도 bitmap 을 얹는다.
        // 다음 단계에서 이 중심을 기준으로 barrel 만 회전시킬 예정이다.
        barrelRect.set(
            x - BARREL_SIZE / 2f,
            y - BARREL_SIZE / 2f,
            x + BARREL_SIZE / 2f,
            y + BARREL_SIZE / 2f,
        )
        canvas.withRotation(angle, x, y) {
            canvas.drawBitmap(barrelBitmap, null, barrelRect, null)
        }
    }

    companion object {
        private const val BASE_SIZE = 100f
        private const val BARREL_SIZE = 110f
    }
}
