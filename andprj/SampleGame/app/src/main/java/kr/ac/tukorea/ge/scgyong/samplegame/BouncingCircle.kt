package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import kotlin.math.abs
import kotlin.random.Random

private const val GRAVITY = 1800f // 중력 가속도. 단위는 unit/s^2

class BouncingCircle(gctx: GameContext) {
    val radius = Random.nextFloat() * 100 + 100f // 반지름을 100~200 사이의 랜덤한 값으로 설정
    val x = Random.nextFloat() * (gctx.worldWidth - 2 * radius) + radius // 원이 화면 밖으로 나가지 않도록 위치 설정
    var y = Random.nextFloat() * (gctx.worldHeight - 2 * radius) + radius // 원이 화면 밖으로 나가지 않도록 위치 설정
    var speed = Random.nextFloat() * 1000f - 500f // 속도를 -500~500 사이의 랜덤한 값으로 설정

    val paint = Paint().apply {
        color = Color.rgb( // 랜덤한 색상 설정. 밝은 색이 되도록 64~191 범위에서 랜덤한 값을 사용한다.
            Random.nextInt(128) + 64,
            Random.nextInt(128) + 64,
            Random.nextInt(128) + 64)
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    fun update(gctx: GameContext) {
        // 위치 업데이트
        y += speed * gctx.frameTime

        if (speed > 0 && y >= gctx.worldHeight - radius) {
            speed = -speed
            if (abs(speed) < 20f) {
                speed = -(GRAVITY + 5 * radius * Random.nextFloat()) // 속도가 너무 느려지면 다시 랜덤한 속도로 설정한다.
                Log.d(javaClass.simpleName, "Bounce! speed=$speed")
            }
        }
        speed += GRAVITY * gctx.frameTime // 중력 가속도 적용
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
    }
}