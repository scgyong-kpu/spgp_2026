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

        val mouthX1 = faceCx - faceRadius / 2
        val mouthX2 = faceCx + faceRadius / 2
        val mouthY = faceCy + faceRadius / 2

        // Android Canvas.drawArc 는 startAngle, endAngle 이 아니라 startAngle, sweepAngle 방식이다.
        // 즉 15f 에서 시작해서 150f 만큼 더 그린다는 뜻이며, 끝각이 150f 라는 뜻이 아니다.
        // 같은 호 그리기라도 어떤 시스템은 start~end 를 쓰고 어떤 시스템은 start~sweep 을 쓰므로 API 문서를 확인해야 한다.
        // 예를 들어 Android Canvas, Java AWT Graphics 는 start~sweep 계열이고,
        // 수학 설명이나 SVG/게임 코드 일부는 start~end 처럼 시작각과 끝각으로 설명하는 경우가 많다.
        // useCenter=false 면 부채꼴이 아니라 호만 그려지고, 양의 sweepAngle 은 시계 방향으로 진행된다.
        canvas.drawArc(mouthX1, eyeCy, mouthX2, mouthY, 15f, 150f, false, strokePaint)
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
