package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import kotlin.math.abs
import kotlin.random.Random

private const val GRAVITY = 1800f // 중력 가속도. 단위는 unit/s^2

class BouncingCircle(gctx: GameContext) : IGameObject {
    val radius = Random.nextFloat() * 100 + 100f // 반지름을 100~200 사이의 랜덤한 값으로 설정
    val x = Random.nextFloat() * (gctx.worldWidth - 2 * radius) + radius // 원이 화면 밖으로 나가지 않도록 위치 설정
    var y = Random.nextFloat() * (gctx.worldHeight - 2 * radius) + radius // 원이 화면 밖으로 나가지 않도록 위치 설정
    var speed = Random.nextFloat() * 1000f - 500f // 속도를 -500~500 사이의 랜덤한 값으로 설정

    val text = "%.1f".format(radius) // 반지름 값을 텍스트로 표시하기 위한 문자열이다.
    var textOffsetX = 0f // 텍스트가 원의 중심에 오도록 조정하기 위한 x축 오프셋이다.
    var textOffsetY = 0f // 텍스트가 원의 중심에 오도록 조정하기 위한 y축 오프셋이다.

    val paint = Paint().apply {
        color = Color.rgb( // 랜덤한 색상 설정. 밝은 색이 되도록 64~191 범위에서 랜덤한 값을 사용한다.
            Random.nextInt(128) + 64,
            Random.nextInt(128) + 64,
            Random.nextInt(128) + 64)
        style = Paint.Style.STROKE
        strokeWidth = radius / 20f // 선의 두께를 원의 반지름의 1/20로 설정한다.
        textSize = radius / 2f // 텍스트 크기를 원의 반지름의 절반으로 설정한다.
    }

    init {
        val textWidth = paint.measureText(text) // 텍스트의 너비를 측정한다.
        val textHeight = paint.fontMetrics.run { ascent + descent } // 텍스트의 높이를 측정한다.
        textOffsetX = -textWidth / 2f // 텍스트의 너비의 절반만큼 왼쪽으로 이동하여 원의 중심에 오도록 한다.
        textOffsetY = -textHeight / 2f // 텍스트의 높이의 절반만큼 위로 이동하여 원의 중심에 오도록 한다.
    }


    override fun update(gctx: GameContext) {
        // 위치 업데이트
        y += speed * gctx.frameTime

        if (speed > 0 && y >= gctx.worldHeight - radius) {
            speed = -speed
            if (abs(speed) < 20f) {
                speed = -(GRAVITY + 5 * radius * Random.nextFloat()) // 속도가 너무 느려지면 다시 랜덤한 속도로 설정한다.
                Log.d(javaClass.simpleName, "Shoot again! speed=$speed")
            }
        }
        speed += GRAVITY * gctx.frameTime // 중력 가속도 적용
    }

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
        canvas.drawText(text, x + textOffsetX, y + textOffsetY, paint)
    }
}
