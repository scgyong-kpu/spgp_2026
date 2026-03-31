package kr.ac.tukorea.ge.scgyong.samplegame.game.scene.main

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.cos
import kotlin.math.sin

class Bullet(
    private var x: Float,
    private var y: Float,
    private val angle: Float,
) : IGameObject {
    override fun update(gctx: GameContext) {
        x += SPEED * cos(angle) * gctx.frameTime
        y += SPEED * sin(angle) * gctx.frameTime

        if (x < -RADIUS || x > gctx.metrics.width + RADIUS || y < -RADIUS || y > gctx.metrics.height + RADIUS) {
            // Bullet 은 삭제 정책을 모르고, 현재 Scene 의 World 에 remove() 만 요청한다.
            // 실제 삭제를 바로 할지, 나중에 할지는 World 가 결정한다.
            (gctx.scene as? MainScene)?.world?.remove(this, MainScene.Layer.BULLET)
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, RADIUS, paint)
    }

    companion object {
        private const val SPEED = 1000f
        private const val RADIUS = 30f

        private val paint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.RED
        }
    }
}
