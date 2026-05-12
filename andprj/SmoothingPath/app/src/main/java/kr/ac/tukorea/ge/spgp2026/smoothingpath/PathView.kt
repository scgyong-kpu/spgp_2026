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
    var curved: Boolean = false
        set(value) {
            field = value
            buildPath()
            invalidate()
        }
    var callback: Callback? = null
    val points = arrayListOf<PointF>()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action
        if (action != MotionEvent.ACTION_DOWN &&
            action != MotionEvent.ACTION_MOVE) return false

        val x = event.x
        val y = event.y
        val pt = PointF(x, y)
        points.add(pt)
        if (points.size == 1) {
            planePosition.set(pt)
            planeAngle = 0f
        } else if (points.size == 2) {
            val dx = x - planePosition.x
            val dy = y - planePosition.y
            val angleRadian = atan2(dy, dx)
            planeAngle = Math.toDegrees(angleRadian.toDouble()).toFloat() + 90f
        }
        buildPath()
        callback?.onSizeChanged(points.size)
        invalidate()

        Log.d(javaClass.simpleName, "Count=${points.size} Points=$points")

        return true
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
        Log.d(javaClass.simpleName, "buildPath: closed=$closed curved=$curved")
        path.reset()
        if (points.size < 2) return
        if (curved) {
            buildCurvedPath()
        } else {
            buildStraightPath()
        }
        if (closed) {
            path.close()
        }
        // Path 내용을 다시 만들었으므로, PathMeasure 도 같은 Path 를 바라보도록 갱신한다.
        pathMeasure.setPath(path, false)
    }

    private fun buildStraightPath() {
        val pt = points[0]
        path.moveTo(pt.x, pt.y)
        for (i in 1..<points.size) {
            val pt = points[i]
            path.lineTo(pt.x, pt.y)
        }
    }

    private fun buildCurvedPath() {
        // Catmull-Rom 방식으로 입력 점을 지나가는 부드러운 곡선을 만든다.
        // Android Path 는 cubicTo() 에 control point 2개와 도착점을 넘겨야 하므로,
        // 주변 점들을 참고해 각 구간의 control point 를 자동으로 계산한다.
        val first = points[0]
        path.moveTo(first.x, first.y)

        for (i in 0 until points.size - 1) {
            // p1 -> p2 구간을 그리기 위해 앞쪽 점 p0, 뒤쪽 점 p3 을 함께 본다.
            // 양 끝에서는 더 앞/뒤의 점이 없으므로 현재 끝점을 한 번 더 사용한다.
            val p0 = points.getOrElse(i - 1) { points[i] }
            val p1 = points[i]
            val p2 = points[i + 1]
            val p3 = points.getOrElse(i + 2) { points[i + 1] }

            // 1/6 은 Catmull-Rom spline 을 cubic Bezier 로 바꿀 때 쓰는 기본 계수다.
            // p0->p2 방향은 p1 에서 출발하는 기울기를 만들고,
            // p1->p3 방향은 p2 로 들어오는 기울기를 만든다.
            val cp1x = p1.x + (p2.x - p0.x) / 6f
            val cp1y = p1.y + (p2.y - p0.y) / 6f
            val cp2x = p2.x - (p3.x - p1.x) / 6f
            val cp2y = p2.y - (p3.y - p1.y) / 6f

            path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
        }
    }

    fun clear() {
        planeAngle = 0f
        animator.cancel()
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
