package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
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

        // drawBitmap 의 x, y 는 비트맵 중심이 아니라 왼쪽 위 기준점이다.
        // 그래서 View 중앙에 맞추려면 비트맵 크기의 절반만큼 빼서 보정해야 한다.
        val x = (width - soccerBallBitmap.width) / 2.0f
        val y = (height - soccerBallBitmap.height) / 2.0f
        canvas.drawBitmap(soccerBallBitmap, x, y, null)
        Log.d(javaClass.simpleName, "Bitmap Size: ${soccerBallBitmap.width}x${soccerBallBitmap.height}")

        // Java 였다면 getWidth() 나 getHeight() 메서드를 호출해야 했겠지만,
        // Kotlin에서는 width 나 height 프로퍼티로 접근할 수 있다.
        // float x = (getWidth() - soccerBallBitmap.getWidth()) / 2.0f;
        // float y = (getHeight() - soccerBallBitmap.getHeight()) / 2.0f;
        // canvas.drawBitmap(soccerBallBitmap, x, y, null);
    }
}
