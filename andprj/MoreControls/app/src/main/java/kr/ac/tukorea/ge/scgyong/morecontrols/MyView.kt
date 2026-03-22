package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    // 면의 내부 색을 채우는 Paint.
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 235, 59)
        style = Paint.Style.FILL
    }

    // 테두리를 그리는 Paint.
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    private var faceCx = 0f
    private var faceCy = 0f
    private var faceRadius = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // View 의 실제 width, height 가 정해지거나 바뀌면 onSizeChanged 가 호출된다.
        // orientation|screenSize 를 configChanges 로 직접 처리하겠다고 선언하면
        // Activity 를 다시 만들지는 않고, 이런 식으로 현재 View 에 새 크기 정보가 전달된다.
        Log.d(javaClass.simpleName, "onSizeChanged: (w=$w, h=$h) <= (oldw=$oldw, oldh=$oldh)")
        calculateFaceGeometry()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // onDraw 는 계산보다 실제 그리기에 집중한다.
        val eyeRadius = faceRadius / 4f
        val leftEyeCx = faceCx - faceRadius / 3f
        val rightEyeCx = faceCx + faceRadius / 3f
        val eyeCy = faceCy - faceRadius / 4f

        canvas.drawCircle(faceCx, faceCy, faceRadius, fillPaint)
        canvas.drawCircle(faceCx, faceCy, faceRadius, strokePaint)
        canvas.drawCircle(leftEyeCx, eyeCy, eyeRadius, strokePaint)
        canvas.drawCircle(rightEyeCx, eyeCy, eyeRadius, strokePaint)
    }

    private fun calculateFaceGeometry() {
        val contentLeft = paddingLeft.toFloat()
        val contentTop = paddingTop.toFloat()
        val contentRight = width - paddingRight.toFloat()
        val contentBottom = height - paddingBottom.toFloat()

        val contentWidth = contentRight - contentLeft
        val contentHeight = contentBottom - contentTop

        // 가로세로 중 더 짧은 쪽에 맞춰 반지름을 잡으면 타원이 아니라 원이 된다.
        faceCx = (contentLeft + contentRight) / 2f
        faceCy = (contentTop + contentBottom) / 2f
        faceRadius = minOf(contentWidth, contentHeight) / 2f

        Log.d(
            javaClass.simpleName,
            "calculateFaceGeometry: center=($faceCx, $faceCy), faceRadius=$faceRadius",
        )
    }
}
