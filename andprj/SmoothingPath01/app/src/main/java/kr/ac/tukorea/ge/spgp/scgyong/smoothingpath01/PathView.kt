package kr.ac.tukorea.ge.spgp.scgyong.smoothingpath01

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColorInt

class PathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
): View(context, attrs, defStyleAttr) {
    val points = arrayListOf<PointF>()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action != MotionEvent.ACTION_DOWN) return false

        val x = event.x
        val y = event.y
        val pt = PointF(x, y)
        points.add(pt)
        buildPath()
        invalidate()

        Log.d(javaClass.simpleName, "Count=${points.size} Points=$points")

        return super.onTouchEvent(event)
    }

    val path = Path()

    override fun onDraw(canvas: Canvas) {

        if (points.isEmpty()) return
        if (points.size == 1) {
            val pt = points[0]
            canvas.drawCircle(pt.x, pt.y, 5f, paint)
            return
        }

        canvas.drawPath(path, paint)
        super.onDraw(canvas)
    }

    private fun buildPath() {
        path.reset()
        if (points.size < 2) return
        val pt = points[0]
        path.moveTo(pt.x, pt.y)
        for (i in 1..<points.size) {
            val pt = points[i]
            path.lineTo(pt.x, pt.y)
        }
    }

    val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = "#1239FE".toColorInt()
    }

}
