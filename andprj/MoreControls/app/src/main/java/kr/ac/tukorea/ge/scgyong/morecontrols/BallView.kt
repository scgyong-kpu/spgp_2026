package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import android.view.View

class BallView(context: Context) : View(context) {

    // Java 였다면 getBitmapOptions() 를 lazy getter 비슷하게 만들어 이런 식으로 쓸 수 있다.
    // private Bitmap soccerBallBitmap =
    //     BitmapFactory.decodeResource(getResources(), R.mipmap.soccer_ball_240, getBitmapOptions());
    //
    // private BitmapFactory.Options bitmapOptions;
    //
    // private BitmapFactory.Options getBitmapOptions() {
    //     if (bitmapOptions == null) {
    //         bitmapOptions = new BitmapFactory.Options();
    //         bitmapOptions.inScaled = false;
    //     }
    //     return bitmapOptions;
    // }
    //
    // Kotlin 에서는 apply 로 옵션 객체를 더 간결하게 준비할 수 있다.
    private val bitmapOptions = BitmapFactory.Options().apply {
        // density 에 따른 자동 확대/축소를 끄고, 파일의 원래 픽셀 크기 그대로 읽는다.
        inScaled = false
    }

    // 이 시점에는 이미 View 가 context 와 연결된 상태라 resources 접근이 가능하다.
    // 다만 View 생성 때마다 decodeResource 가 실행되므로, 동작 가능 여부와 별개로 비용은 따로 생각해야 한다.
    private val soccerBallBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.mipmap.soccer_ball_240, bitmapOptions)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 화면 폭의 1/10 크기로 공을 그린다.
        val cx = width / 2.0f
        val cy = height / 2.0f
        val ballRadius = cx / 10
        val ballRect = RectF(cx - ballRadius, cy - ballRadius, cx + ballRadius, cy + ballRadius)
        canvas.drawBitmap(soccerBallBitmap, null, ballRect, null)
        Log.d(javaClass.simpleName, "Bitmap Size: ${ballRect.f2String}")
    }
}

// RectF 좌표값(left, top, right, bottom)을 소수 둘째 자리까지 보이게 문자열로 바꾼다.
// 로그에서 사각형 좌표를 읽기 쉽게 보려고 쓰는 extension property 이다.
val RectF.f2String: String
    get() = "(${"%.2f".format(left)}, ${"%.2f".format(top)}, ${"%.2f".format(right)}, ${"%.2f".format(bottom)})"
