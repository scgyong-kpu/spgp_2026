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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 화면 폭의 1/10 크기로 공을 그린다.
        val cx = width / 2.0f
        val cy = height / 2.0f
        val ballRadius = cx / 10

        // 객체는 한 번만 만들어서 재사용한다. onDraw()가 호출될 때마다 새로 만들면 GC 부담이 커진다.
        // 여기서는 생성되어 있는 객체에 값을 설정하기만 한다.
        soccerBallRect.set(cx - ballRadius, cy - ballRadius, cx + ballRadius, cy + ballRadius)
        canvas.drawBitmap(soccerBallBitmap, null, soccerBallRect, null)
        Log.d(javaClass.simpleName, "Bitmap Size: ${soccerBallRect.f2String}")
    }
}

// RectF 좌표값(left, top, right, bottom)을 소수 둘째 자리까지 보이게 문자열로 바꾼다.
// 로그에서 사각형 좌표를 읽기 쉽게 보려고 쓰는 extension property 이다.
val RectF.f2String: String
    get() = "(${"%.2f".format(left)}, ${"%.2f".format(top)}, ${"%.2f".format(right)}, ${"%.2f".format(bottom)})"
