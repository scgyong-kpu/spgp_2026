package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    // 채워진 면의 내부 색을 그릴 때 사용하는 Paint.
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 182, 193)
        style = Paint.Style.FILL
    }

    // 면의 테두리를 그릴 때 사용하는 Paint.
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    fun setStrokeWidth(width: Float) {
        strokePaint.strokeWidth = width
        invalidate()
    }

    fun setStrokeCap(cap: Paint.Cap) {
        strokePaint.strokeCap = cap
        invalidate()
    }

    fun setStrokeJoin(join: Paint.Join) {
        strokePaint.strokeJoin = join
        invalidate()
    }

    val rect = Rect()

    init {
        // 처음에는 생성 시점에 한 번만 계산해 두면 더 효율적일 것 같아 보인다.
        // 하지만 이 시점에는 아직 View 의 실제 width/height 가 정해지지 않아,
        // 기대한 좌표가 나오지 않고 결과도 생각대로 보이지 않는다.
        calculateRect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 그래서 지금 상태는 "이렇게 옮기면 안 된다"는 비교 예제에 가깝다.
        // 실제 크기가 정해진 뒤에 다시 계산하는 더 올바른 위치는 다음 단계에서 다룬다.
        canvas.drawRect(rect, fillPaint)
        canvas.drawRect(rect, strokePaint)
    }

    private fun calculateRect() {
        // padding 을 제외한 내부 영역을 먼저 계산한 뒤,
        // 그 안쪽에 한 칸 여백을 둔 사각형 좌표를 Rect 로 만든다.
        val contentLeft = paddingLeft.toFloat()
        val contentTop = paddingTop.toFloat()
        val contentRight = width - paddingRight.toFloat()
        val contentBottom = height - paddingBottom.toFloat()

        val contentWidth = contentRight - contentLeft
        val contentHeight = contentBottom - contentTop

        val w6 = contentWidth / 6
        val h6 = contentHeight / 6
        val x1 = contentLeft + w6
        val x2 = contentRight - w6
        val y1 = contentTop + h6
        val y2 = contentBottom - h6

        // Rect 는 left, top, right, bottom 네 값으로 사각형 영역을 나타내는 Android 기본 클래스이다.
        rect.set(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
    }
}
