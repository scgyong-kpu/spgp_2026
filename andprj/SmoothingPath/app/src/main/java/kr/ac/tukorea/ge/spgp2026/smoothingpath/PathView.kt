package kr.ac.tukorea.ge.spgp.scgyong.smoothingpath01

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.telecom.Call
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColorInt
import kr.ac.tukorea.ge.spgp2026.smoothingpath.R

class PathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
): View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener {

    val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.plane_240)

    interface Callback {
        fun onSizeChanged(size: Int)
    }

    var closed: Boolean = false
        set(value) {
            field = value
            buildPath()
            invalidate()
        }
    var callback: Callback? = null
    val points = arrayListOf<PointF>()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action != MotionEvent.ACTION_DOWN) return false

        val x = event.x
        val y = event.y
        val pt = PointF(x, y)
        points.add(pt)
        buildPath()
        callback?.onSizeChanged(points.size)
        invalidate()

        Log.d(javaClass.simpleName, "Count=${points.size} Points=$points")

        return super.onTouchEvent(event)
    }

    val path = Path()

    override fun onDraw(canvas: Canvas) {

        if (points.isEmpty()) return

        val first = points[0]
        canvas.drawBitmap(bitmap,
            first.x - bitmap.width / 2,
            first.y - bitmap.height / 2, null)
        if (points.size == 1) {
            canvas.drawCircle(first.x, first.y, 5f, paint)
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
        if (closed) {
            path.close()
        }
    }

    fun clear() {
        points.clear()
        buildPath()
        callback?.onSizeChanged(points.size)
        invalidate()
    }

    val animator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000L
            // PathView 가 AnimatorUpdateListener 를 직접 구현하므로,
            // 람다 대신 바깥 PathView 인스턴스(this@PathView)를 listener 로 넘길 수 있다.
            addUpdateListener(this@PathView)
        }
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val value = animation.animatedValue as Float
        val pos = FloatArray(2)
        PathMeasure(path, false).getPosTan(value, pos, null)
        Log.d(javaClass.simpleName, "Anim value=$value pos=(${pos[0]}, ${pos[1]})")
    }

    fun startPathAnimation() {
        val pathLength = PathMeasure(path, false).length
        animator.setFloatValues(0f, pathLength)
        animator.start()
    }

    val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = "#1239FE".toColorInt()
    }

}
