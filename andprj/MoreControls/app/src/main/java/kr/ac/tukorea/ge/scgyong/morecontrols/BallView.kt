package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.View

class BallView(context: Context) : View(context) {
    // 지금 BallView 는 AnotherActivity 에서 BallView(this) 형태로 코드에서 직접 만든다.
    // XML inflation 경로를 쓰지 않으므로 Context 생성자 하나만 있어도 된다.

    // 이 시점에는 이미 View 가 context 와 연결된 상태라 resources 접근이 가능하다.
    // 다만 View 생성 때마다 decodeResource 가 실행되므로, 동작 가능 여부와 별개로 비용은 따로 생각해야 한다.
    private val soccerBallBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.mipmap.soccer_ball_240)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // drawBitmap 의 x, y 는 비트맵 중심이 아니라 왼쪽 위 기준점이다.
        // 그래서 View 중앙에 맞추려면 비트맵 크기의 절반만큼 빼서 보정해야 한다.
        val x = (width - soccerBallBitmap.width) / 2.0f
        val y = (height - soccerBallBitmap.height) / 2.0f
        canvas.drawBitmap(soccerBallBitmap, x, y, null)

        // Java 였다면 getWidth() 나 getHeight() 메서드를 호출해야 했겠지만,
        // Kotlin에서는 width 나 height 프로퍼티로 접근할 수 있다.
        // float x = (getWidth() - soccerBallBitmap.getWidth()) / 2.0f;
        // float y = (getHeight() - soccerBallBitmap.getHeight()) / 2.0f;
        // canvas.drawBitmap(soccerBallBitmap, x, y, null);
    }
}
