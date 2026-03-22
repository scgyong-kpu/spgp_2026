package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        Log.d(javaClass.simpleName, "onSizeChanged: (w=$w, h=$h) <= (oldw=$oldw, oldh=$oldh)")
        calculateRect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // onDraw 는 계산보다 실제 그리기에 집중한다.
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
        Log.d(javaClass.simpleName, "calculateRect: $rect")
    }
}
