package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.view.View
import androidx.core.graphics.withTranslation
import kotlin.math.min

class BallView(context: Context) : View(context) {
    // 실제 화면 위에 가상 좌표계를 어디에 둘지 나타내는 이동량이다.
    // 화면 비율이 9:16과 다를 수 있으므로 남는 공간을 가운데 정렬하기 위해 쓴다.
    private val transformOffset = PointF()

    // 가상 좌표계의 1칸이 실제 화면에서 몇 픽셀이 될지를 나타내는 배율이다.
    private var transformScale = 0f

    private val bitmapOptions = BitmapFactory.Options().apply {
        inScaled = false // density 에 따른 자동 확대/축소를 끄고, 파일의 원래 픽셀 크기 그대로 읽는다.
    }
    private val soccerBallBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.mipmap.soccer_ball_240, bitmapOptions)

    private val bgBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.mipmap.block_9x16, bitmapOptions)

    // 배경과 공을 그릴 목적 사각형이다.
    // 모든 좌표계가 9x16 기준으로 동작하므로, 상수를 사용할 수 있다
    // 여기서 Design Time 에 결정된 값이 그대로 사용된다
    private val bgRect = RectF(0f, 0f, 9f, 16f)
    private val soccerBallRect = RectF(4f, 7.5f, 5f, 8.5f)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 9 x 16 가상 좌표계를 실제 화면에 "모두 보이게" 맞추기 위해,
        // 가로 기준 배율과 세로 기준 배율 중 더 작은 값을 선택한다.
        val scaleX = w / 9f
        val scaleY = h / 16f
        transformScale = min(scaleX, scaleY)

        // 가상 좌표계 전체가 차지하는 실제 픽셀 크기이다.
        val contentWidth = 9f * transformScale
        val contentHeight = 16f * transformScale

        // 남는 공간이 있으면 가운데에 오도록 offset 을 계산한다.
        transformOffset.x = (w - contentWidth) / 2f
        transformOffset.y = (h - contentHeight) / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 먼저 실제 화면 좌표계를 가상 좌표계의 원점 위치로 옮긴다.
        // 그 다음 1칸 = transformScale 픽셀이 되도록 확대한다.
        // 이 블록 안에서는 9 x 16 가상 좌표계 기준으로 그대로 그리면 된다.
        canvas.withTranslation(transformOffset.x, transformOffset.y) {
            scale(transformScale, transformScale)

            // 배경을 먼저 깔고, 그 위에 공을 그려야 공이 배경에 가려지지 않는다.
            drawBitmap(bgBitmap, null, bgRect, null)
            drawBitmap(soccerBallBitmap, null, soccerBallRect, null)
        }
    }
}

// RectF 좌표값(left, top, right, bottom)을 소수 둘째 자리까지 보이게 문자열로 바꾼다.
// 로그에서 목적 사각형 좌표를 읽기 쉽게 보려고 쓰는 extension property 이다.
val RectF.f2String: String
    get() = "(${"%.2f".format(left)}, ${"%.2f".format(top)}, ${"%.2f".format(right)}, ${"%.2f".format(bottom)})"
