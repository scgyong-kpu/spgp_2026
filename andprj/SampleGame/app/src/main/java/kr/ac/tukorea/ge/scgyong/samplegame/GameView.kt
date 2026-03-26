package kr.ac.tukorea.ge.scgyong.samplegame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withMatrix


// 게임이 내부적으로 사용할 가상 좌표계의 크기이다.
// 실제 폰 화면 크기와는 별개로, 게임 안에서는 900 x 1600 공간이 있다고 생각하고 그린다.
private const val VIRTUAL_WIDTH = 900f
private const val VIRTUAL_HEIGHT = 1600f

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    // 가상 좌표계 전체를 둘러싸는 테두리 사각형이다.
    // 0,0 에서 시작해 900,1600 까지의 공간이 게임의 내부 좌표계라는 뜻이 된다.
    private val borderRect = RectF(0f, 0f, VIRTUAL_WIDTH, VIRTUAL_HEIGHT)

    // 테두리만 그리는 Paint 이다.
    // 일단은 게임 월드의 범위를 눈으로 확인하기 위한 가장 단순한 설정만 둔다.
    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    // 가상 좌표계를 실제 화면에 맞게 옮기고 확대/축소하는 변환 행렬이다.
    // onDraw 때 매번 새로 계산하지 않고, 화면 크기가 바뀔 때 미리 준비해 둔다.
    private val transformMatrix = Matrix()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 실제 화면의 가로폭이 900 이라면 1칸이 몇 픽셀인지,
        // 실제 화면의 세로높이가 1600 이라면 1칸이 몇 픽셀인지 각각 계산한다.
        val scaleX = w / VIRTUAL_WIDTH
        val scaleY = h / VIRTUAL_HEIGHT

        // 900 x 1600 가상 화면 전체가 잘리지 않고 모두 보이게 하려면
        // 둘 중 더 작은 배율을 선택해야 한다.
        val scale = minOf(scaleX, scaleY)

        // 선택된 배율에서 가상 화면 전체가 실제로 차지하는 픽셀 크기이다.
        val contentWidth = VIRTUAL_WIDTH * scale
        val contentHeight = VIRTUAL_HEIGHT * scale

        // 실제 화면이 더 크면 남는 공간이 생긴다.
        // 그 공간을 좌우 또는 상하에 반씩 나눠 주면 가운데 정렬이 된다.
        val offsetX = (w - contentWidth) / 2f
        val offsetY = (h - contentHeight) / 2f

        // 이전 변환은 지우고,
        // 먼저 offset 만큼 이동한 뒤,
        // 그 다음 scale 배율만큼 확대/축소하는 최종 변환을 만든다.
        transformMatrix.reset()
        transformMatrix.postTranslate(offsetX, offsetY)
        transformMatrix.postScale(scale, scale, offsetX, offsetY)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // withMatrix 안에서는 실제 화면 좌표가 아니라
        // 900 x 1600 가상 좌표계 기준으로 그린다고 생각하면 된다.
        canvas.withMatrix(transformMatrix) {
            // 900 x 1600 가상 좌표계 기준으로 그리면 된다.
            drawRect(borderRect, borderPaint)
        }
        // withMatrix 블럭이 끝나면 canvas.restore() 가 자동으로 호출되어서
        // 다시 실제 화면 좌표계로 돌아온다.
    }
}
