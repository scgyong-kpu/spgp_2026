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
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.smoothingpath.R
import kotlin.math.atan2

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
        if (points.size == 1) {
            planePosition.set(pt)
        }
        buildPath()
        callback?.onSizeChanged(points.size)
        invalidate()

        Log.d(javaClass.simpleName, "Count=${points.size} Points=$points")

        return super.onTouchEvent(event)
    }

    val path = Path()
    // PathMeasure 는 Path 위의 거리(length)를 실제 좌표로 바꿔 주는 도구다.
    // 애니메이션 중에는 매 프레임 쓰이므로 매번 새로 만들지 않고 멤버로 재사용한다.
    val pathMeasure = PathMeasure()
    // getPosTan() 은 결과 좌표를 FloatArray 에 채워 주므로, 이 배열도 함께 재사용한다.
    val pathPosition = FloatArray(2)
    val pathTangent = FloatArray(2)
    val planePosition = PointF()
    var planeAngle = 0f

    override fun onDraw(canvas: Canvas) {

        if (points.isEmpty()) return

        val first = points[0]
        canvas.withRotation(planeAngle, planePosition.x, planePosition.y) {
            drawBitmap(bitmap,
                planePosition.x - bitmap.width / 2,
                planePosition.y - bitmap.height / 2, null)
        }
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
        // Path 내용을 다시 만들었으므로, PathMeasure 도 같은 Path 를 바라보도록 갱신한다.
        pathMeasure.setPath(path, false)
    }

    fun clear() {
        points.clear()
        buildPath()
        callback?.onSizeChanged(points.size)
        invalidate()
    }

    val animator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            // PathView 가 AnimatorUpdateListener 를 직접 구현하므로,
            // 람다 대신 바깥 PathView 인스턴스(this@PathView)를 listener 로 넘길 수 있다.
            addUpdateListener(this@PathView)
        }
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val value = animation.animatedValue as Float
        pathMeasure.getPosTan(value, pathPosition, pathTangent)
        planePosition.set(pathPosition[0], pathPosition[1])
        planeAngle = Math.toDegrees(atan2(pathTangent[1], pathTangent[0]).toDouble()).toFloat() + 90f
        invalidate()
    }

    fun startPathAnimation() {
        val pathLength = pathMeasure.length
        animator.setFloatValues(0f, pathLength)
        animator.duration = pathLength.toLong() // 애니메이션의 길이를 Path 의 길이에 비례하도록 조절한다.
        animator.start()
    }

    val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = "#1239FE".toColorInt()
    }

}
