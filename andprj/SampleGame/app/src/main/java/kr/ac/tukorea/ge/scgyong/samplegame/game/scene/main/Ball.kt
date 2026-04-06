package kr.ac.tukorea.ge.scgyong.samplegame.game.scene.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.scgyong.samplegame.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Ball(
    gctx: GameContext,
    centerX: Float,
    centerY: Float,
    angleDegree: Float,
) : Sprite(gctx, R.mipmap.soccer_ball_240) {
    init {
        x = centerX
        y = centerY
        width = SIZE.toFloat()
        height = SIZE.toFloat()
    }

    // dx, dy 를 직접 넘기지 않고 angle 로부터 같은 SPEED 크기의 속도 벡터를 만든다.
    // 이렇게 하면 시작 방향만 다르고 속력 자체는 모든 Ball 이 같아진다.
    private val radian = Math.toRadians(angleDegree.toDouble())
    var dx = (cos(radian) * SPEED).toFloat()
    var dy = (sin(radian) * SPEED).toFloat()

    override fun update(gctx: GameContext) {
        val offsetX = dx * gctx.frameTime
        val offsetY = dy * gctx.frameTime
        x += offsetX
        y += offsetY

        if (x - width / 2f < 0f || x + width / 2f > gctx.metrics.width) {
            dx = -dx
            x -= offsetX
        }

        if (y - height / 2f < 0f || y + height / 2f > gctx.metrics.height) {
            dy = -dy
            y -= offsetY
        }
    }

    fun debugString(): String {
        return "x=$x, y=$y, dx=$dx, dy=$dy"
    }

    // Kotlin 은 class 소속 static 변수/상수/함수 를 companion object 안에 선언한다.
    companion object {
        const val SIZE = 200
        const val SPEED = 700

        fun random(gctx: GameContext): Ball {
            val centerX = Random.nextFloat() * (gctx.metrics.width - SIZE) + SIZE / 2f
            val centerY = Random.nextFloat() * (gctx.metrics.height - SIZE) + SIZE / 2f
            val angleDegree = Random.nextFloat() * 360f
            return Ball(gctx, centerX, centerY, angleDegree)
        }
    }
}
