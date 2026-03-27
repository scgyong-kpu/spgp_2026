package kr.ac.tukorea.ge.scgyong.samplegame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.sin

class Ball(
    context: Context,
    centerX: Float,
    centerY: Float,
    angle_degree: Float,
) {
    val rect = RectF(
        centerX - SIZE / 2f,
        centerY - SIZE / 2f,
        centerX + SIZE / 2f,
        centerY + SIZE / 2f,
    )

    // Ball 객체가 여러 개 생기면 bitmap loading 이 여러 번 일어나서 비효율적이다.
    // 그래서 bitmap 을 companion object 안에 선언해서 모든 Ball 객체가 하나의 bitmap 을 공유하는
    // 방법을 사용할 수도 있지만, 나중에 다른 방법으로 개선할 예정이므로, 이번에는 하지 않기로 한다.
    private val bitmap: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.mipmap.soccer_ball_240)


    // dx, dy 를 직접 넘기지 않고 angle 로부터 같은 SPEED 크기의 속도 벡터를 만든다.
    // 이렇게 하면 시작 방향만 다르고 속력 자체는 모든 Ball 이 같아진다.
    val radian = Math.toRadians(angle_degree.toDouble())
    var dx = (cos(radian) * SPEED).toFloat()
    var dy = (sin(radian) * SPEED).toFloat()

    fun update(worldWidth: Float, worldHeight: Float) {
        rect.offset(dx, dy)

        if (rect.left < 0f || rect.right > worldWidth) {
            dx = -dx
            rect.offset(dx, 0f)
        }

        if (rect.top < 0f || rect.bottom > worldHeight) {
            dy = -dy
            rect.offset(0f, dy)
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    fun debugString(): String {
        return "rect=$rect, dx=$dx, dy=$dy"
    }

    // Kotlin 은 class 소속 static 변수/상수/함수 를 companion object 안에 선언한다.
    companion object {
        const val SIZE = 200 // 공의 크기는 200 unit
        const val SPEED = 700/60f // 초당 700 unit 를 이동하는 속도. update() 가 1/60 초마다 호출되므로 60으로 나눈 값을 사용한다.
    }
}
