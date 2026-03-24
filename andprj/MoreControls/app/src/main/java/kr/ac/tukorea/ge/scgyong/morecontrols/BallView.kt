package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import android.view.View

class BallView(context: Context) : View(context) {

    private val bitmapOptions = BitmapFactory.Options().apply {
        inScaled = false // density 에 따른 자동 확대/축소를 끄고, 파일의 원래 픽셀 크기 그대로 읽는다.
    }
    private val soccerBallBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.mipmap.soccer_ball_240, bitmapOptions)

    // 공을 그릴 사각형 영역을 나타내는 RectF 객체이다. 
    // onDraw()가 호출될 때마다 새로 만들지 않고 한번만 만들어서 재사용한다.
    private val soccerBallRect = RectF()

    // 배경 블록 이미지와, 그것을 View 전체에 늘려 그릴 목적 사각형이다.
    // 이 역시 onDraw() 안에서 새로 만들지 않고 멤버로 재사용한다.
    private val bgBitmap =
        BitmapFactory.decodeResource(resources, R.mipmap.block_9x16, bitmapOptions)
    private val bgRect = RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 화면 폭의 1/9 크기로 공을 그린다.
        val cx = w / 2.0f
        val cy = h / 2.0f
        val ballRadius = cx / 9

        // 크기가 정해지거나 바뀌는 시점에 목적 사각형을 계산해 둔다.
        // onDraw()에서는 그리기만 하도록 계산과 렌더링의 역할을 나눈다.
        soccerBallRect.set(cx - ballRadius, cy - ballRadius, cx + ballRadius, cy + ballRadius)

        // 배경은 View 전체를 꽉 채우도록 목적 사각형을 잡는다.
        bgRect.set(0F, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 먼저 배경을 깔고, 그 위에 공을 그려야 공이 배경에 가려지지 않는다.
        canvas.drawBitmap(bgBitmap, null, bgRect, null)
        canvas.drawBitmap(soccerBallBitmap, null, soccerBallRect, null)
    }
}

// RectF 좌표값(left, top, right, bottom)을 소수 둘째 자리까지 보이게 문자열로 바꾼다.
// 로그에서 사각형 좌표를 읽기 쉽게 보려고 쓰는 extension property 이다.
val RectF.f2String: String
    get() = "(${"%.2f".format(left)}, ${"%.2f".format(top)}, ${"%.2f".format(right)}, ${"%.2f".format(bottom)})"
