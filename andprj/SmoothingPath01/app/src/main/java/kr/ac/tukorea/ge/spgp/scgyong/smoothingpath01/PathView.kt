package kr.ac.tukorea.ge.spgp.scgyong.smoothingpath01

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

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
}
