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
    // Paint 는 onDraw 에서 항상 필요한 기본 도구이므로, 프로퍼티 선언과 동시에 바로 만든다.
    // 나중에 따로 채워 넣어야 하는 값이 아니어서 lateinit 이 어울리지 않고,
    // 실제로 거의 즉시 필요하므로 lazy 로 미뤄도 얻는 이점이 거의 없다.
    // 채워진 원의 내부 색을 그릴 때 사용하는 Paint.
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 182, 193)
        style = Paint.Style.FILL
    }

    // 원의 테두리와 Z 모양 선을 그릴 때 사용하는 Paint.
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // onDraw 는 "그리는 일"만 담당하고,
        // 실제 사각형 좌표 계산은 calculateRect() 로 분리해 읽기 쉽게 정리한다.
        // 하지만 여전히, 사각형 객체가 생성되는 곳이 onDraw 에서 호출되는 calculateRect() 라는 점은 변하지 않는다.
        val rect = calculateRect()
        canvas.drawRect(rect, fillPaint)
        canvas.drawRect(rect, strokePaint)
    }

    private fun calculateRect(): Rect {
        // padding 을 제외한 내부 영역을 먼저 계산한 뒤,
        // 그 안쪽에 한 칸 여백을 둔 사각형 좌표를 Rect 로 만들어 돌려준다.
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
        val rect = Rect(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
        return rect
    }
}
