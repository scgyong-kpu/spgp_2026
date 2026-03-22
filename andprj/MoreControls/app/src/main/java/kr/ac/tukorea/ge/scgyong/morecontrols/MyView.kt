package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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

    private val path = Path()

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

        val contentLeft = paddingLeft.toFloat()
        val contentTop = paddingTop.toFloat()
        val contentRight = width - paddingRight.toFloat()
        val contentBottom = height - paddingBottom.toFloat()

        val contentWidth = contentRight - contentLeft
        val contentHeight = contentBottom - contentTop
        val radius = minOf(contentWidth, contentHeight) / 2f
        val centerX = contentLeft + contentWidth / 2f
        val centerY = contentTop + contentHeight / 2f

        canvas.drawCircle(centerX, centerY, radius, fillPaint)
        canvas.drawCircle(centerX, centerY, radius, strokePaint)

        val w6 = contentWidth / 6
        val h6 = contentHeight / 6
        val x1 = contentLeft + w6
        val x2 = contentRight - w6
        val y1 = contentTop + h6
        val y2 = contentBottom - h6

        path.reset()
        path.moveTo(x1, y1)
        path.lineTo(x2, y1)
        path.lineTo(x1, y2)
        path.lineTo(x2, y2)

        canvas.drawPath(path, strokePaint)
    }
}
