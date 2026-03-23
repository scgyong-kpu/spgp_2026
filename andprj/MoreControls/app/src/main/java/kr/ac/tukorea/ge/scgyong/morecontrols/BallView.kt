package kr.ac.tukorea.ge.scgyong.morecontrols

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.content.Context
import android.util.AttributeSet
import android.view.View

class BallView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    // 이 시점에는 이미 View 가 context 와 연결된 상태라 resources 접근이 가능하다.
    // 다만 View 생성 때마다 decodeResource 가 실행되므로, 동작 가능 여부와 별개로 비용은 따로 생각해야 한다.
    private val soccerBallBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.mipmap.soccer_ball_240)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(soccerBallBitmap, 0f, 0f, null)
    }
}
