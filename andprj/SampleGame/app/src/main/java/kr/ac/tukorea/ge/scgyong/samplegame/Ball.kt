package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Ball(
    gctx: GameContext,
    centerX: Float,
    centerY: Float,
    angleDegree: Float,
) : IGameObject {
    val rect = RectF(
        centerX - SIZE / 2f,
        centerY - SIZE / 2f,
        centerX + SIZE / 2f,
        centerY + SIZE / 2f,
    )

    // Ball 객체가 여러 개 생기면 bitmap loading 이 여러 번 일어나서 비효율적이다.
    // 그래서 bitmap 을 companion object 안에 선언해서 모든 Ball 객체가 하나의 bitmap 을 공유하는
    // 방법을 사용할 수도 있지만, 나중에 다른 방법으로 개선할 예정이므로, 이번에는 하지 않기로 한다.
    private val bitmap: Bitmap = gctx.getBitmapResource(R.mipmap.soccer_ball_240)

    // dx, dy 를 직접 넘기지 않고 angle 로부터 같은 SPEED 크기의 속도 벡터를 만든다.
    // 이렇게 하면 시작 방향만 다르고 속력 자체는 모든 Ball 이 같아진다.
    private val radian = Math.toRadians(angleDegree.toDouble())
    var dx = (cos(radian) * SPEED).toFloat()
    var dy = (sin(radian) * SPEED).toFloat()

    override fun update(gctx: GameContext) {
        val offsetX = dx * gctx.frameTime
        val offsetY = dy * gctx.frameTime
        rect.offset(offsetX, offsetY)

        if (rect.left < 0f || rect.right > gctx.worldWidth) {
            dx = -dx
            rect.offset(-offsetX, 0f)
        }

        if (rect.top < 0f || rect.bottom > gctx.worldHeight) {
            dy = -dy
            rect.offset(0f, -offsetY)
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    fun debugString(): String {
        return "rect=$rect, dx=$dx, dy=$dy"
    }

    // Kotlin 은 class 소속 static 변수/상수/함수 를 companion object 안에 선언한다.
    companion object {
        const val SIZE = 200
        const val SPEED = 700

        fun random(gctx: GameContext): Ball {
            val centerX = Random.nextFloat() * (gctx.worldWidth - SIZE) + SIZE / 2f
            val centerY = Random.nextFloat() * (gctx.worldHeight - SIZE) + SIZE / 2f
            val angleDegree = Random.nextFloat() * 360f
            return Ball(gctx, centerX, centerY, angleDegree)
        }
    }
}
