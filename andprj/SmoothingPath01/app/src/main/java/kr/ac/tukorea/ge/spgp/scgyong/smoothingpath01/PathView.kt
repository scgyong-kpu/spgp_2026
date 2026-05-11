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

        Log.d(javaClass.simpleName, "Count=${points.size} Points=$points")

        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {

        if (points.size < 2) return
        val path = Path()
        val pt = points[0]
        path.moveTo(pt.x, pt.y)
        for (i in 1 ..< points.size) {
            val pt = points[i]
            path.lineTo(pt.x, pt.y)
        }

        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = "#1239FE".toColorInt()

        canvas.drawPath(path, paint)
//        canvas.draw(path)
        super.onDraw(canvas)
    }

}
