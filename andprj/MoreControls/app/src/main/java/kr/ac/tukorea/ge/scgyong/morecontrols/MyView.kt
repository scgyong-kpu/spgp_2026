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

        // onDraw 는 계산보다 실제 그리기에 집중하고,
        // 미리 구해 둔 중심점과 반지름을 바탕으로 smiley 하나를 그리게 한다.
        drawSmiley(canvas, faceCx, faceCy, faceRadius, 3)
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

    private fun drawSmiley(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        depth: Int,
    ) {
        // 눈과 입은 모두 얼굴 원의 중심점과 반지름에서 파생되는 상대 위치로 계산한다.
        // 그래서 더 작은 smiley 를 다시 그려도 같은 비율을 유지한 채 축소된다.
        val eyeRadius = radius / 4f
        val leftEyeCx = cx - radius / 3f
        val rightEyeCx = cx + radius / 3f
        val eyeCy = cy - radius / 4f

        val mouthX1 = cx - radius / 2f
        val mouthX2 = cx + radius / 2f
        val mouthY = cy + radius / 2f

        canvas.drawCircle(cx, cy, radius, fillPaint)
        canvas.drawCircle(cx, cy, radius, strokePaint)

        // depth 가 0보다 크면 눈 자리에 더 작은 smiley 를 그리고,
        // 0이면 눈을 단순한 원으로만 그린다.
        // 이렇게 하면 재귀 종료 조건(base case)과 재귀 호출 단계가 코드에서 더 직접적으로 보인다.
        if (depth > 1) {
            drawSmiley(canvas, leftEyeCx, eyeCy, eyeRadius, depth - 1)
            drawSmiley(canvas, rightEyeCx, eyeCy, eyeRadius, depth - 1)
        } else {
            canvas.drawCircle(leftEyeCx, eyeCy, eyeRadius, strokePaint)
            canvas.drawCircle(rightEyeCx, eyeCy, eyeRadius, strokePaint)
        }

        // Android Canvas.drawArc 는 startAngle, endAngle 이 아니라 startAngle, sweepAngle 방식이다.
        // 즉 15f 에서 시작해서 150f 만큼 더 그린다는 뜻이며, 끝각이 150f 라는 뜻이 아니다.
        // 같은 호 그리기라도 어떤 시스템은 start~end 를 쓰고 어떤 시스템은 start~sweep 을 쓰므로 API 문서를 확인해야 한다.
        // 예를 들어 Android Canvas, Java AWT Graphics 는 start~sweep 계열이고,
        // 수학 설명이나 SVG, 일부 게임 코드에서는 start~end 처럼 설명하는 경우도 있다.
        // useCenter=false 면 부채꼴이 아니라 호만 그려지고, 양의 sweepAngle 은 시계 방향으로 진행된다.
        canvas.drawArc(mouthX1, eyeCy, mouthX2, mouthY, 15f, 150f, false, strokePaint)
    }
}
