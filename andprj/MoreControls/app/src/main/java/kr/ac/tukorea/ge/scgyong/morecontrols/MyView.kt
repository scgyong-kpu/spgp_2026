package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.withTranslation

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
        strokeWidth = 0.01f
    }

    private var baseTranslateX = 0f
    private var baseTranslateY = 0f
    private var baseScale = 1f

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
        canvas.drawSmiley(baseTranslateX, baseTranslateY, baseScale, 3)
    }

    private fun calculateFaceGeometry() {
        val contentLeft = paddingLeft.toFloat()
        val contentTop = paddingTop.toFloat()
        val contentRight = width - paddingRight.toFloat()
        val contentBottom = height - paddingBottom.toFloat()

        val contentWidth = contentRight - contentLeft
        val contentHeight = contentBottom - contentTop

        // 정규화 좌표계에서 smiley 는 (0.5, 0.5) 중심, 반지름 0.5 로 그리므로
        // 실제 화면에서는 왼쪽 위로 얼마나 옮길지와 몇 배로 키울지만 기억해 두면 된다.
        baseScale = minOf(contentWidth, contentHeight)
        baseTranslateX = (contentLeft + contentRight - baseScale) / 2f
        baseTranslateY = (contentTop + contentBottom - baseScale) / 2f

        Log.d(
            javaClass.simpleName,
            "calculateFaceGeometry: translate=($baseTranslateX, $baseTranslateY), scale=$baseScale",
        )
    }

    // Canvas receiver extension 버전: 정규화 좌표계 안에서 smiley 자체를 그린다.
    private fun Canvas.drawSmiley(depth: Int = 3) {
        val cx = 0.5f
        val cy = 0.5f
        val radius = 0.5f

        val eyeRadius = radius / 4f
        val leftEyeCx = cx - radius / 3f
        val rightEyeCx = cx + radius / 3f
        val eyeCy = cy - radius / 4f

        drawCircle(cx, cy, radius, fillPaint)
        drawCircle(cx, cy, radius, strokePaint)

        if (depth > 1) {
            drawSmiley(leftEyeCx - eyeRadius, eyeCy - eyeRadius, eyeRadius * 2f, depth - 1)
            drawSmiley(rightEyeCx - eyeRadius, eyeCy - eyeRadius, eyeRadius * 2f, depth - 1)
        } else {
            drawCircle(leftEyeCx, eyeCy, eyeRadius, strokePaint)
            drawCircle(rightEyeCx, eyeCy, eyeRadius, strokePaint)
        }

        val mouthLeft = cx - radius / 2f
        val mouthRight = cx + radius / 2f
        val mouthBottom = cy + radius / 2f

        // Android Canvas.drawArc 는 startAngle, endAngle 이 아니라 startAngle, sweepAngle 방식이다.
        // 즉 15f 에서 시작해서 150f 만큼 더 그린다는 뜻이며, 끝각이 150f 라는 뜻이 아니다.
        // 같은 호 그리기라도 어떤 시스템은 start~end 를 쓰고 어떤 시스템은 start~sweep 을 쓰므로 API 문서를 확인해야 한다.
        // 예를 들어 Android Canvas, Java AWT Graphics 는 start~sweep 계열이고,
        // 수학 설명이나 SVG, 일부 게임 코드에서는 start~end 처럼 설명하는 경우도 있다.
        // useCenter=false 면 부채꼴이 아니라 호만 그려지고, 양의 sweepAngle 은 시계 방향으로 진행된다.
        drawArc(mouthLeft, eyeCy, mouthRight, mouthBottom, 15f, 150f, false, strokePaint)
    }

    // 위치와 크기를 바꿔 같은 smiley 를 다시 그리고 싶을 때 쓰는 overload.
    private fun Canvas.drawSmiley(
        translateX: Float,
        translateY: Float,
        scale: Float,
        depth: Int,
    ) {
        withTranslation(translateX, translateY) {
            scale(scale, scale)
            drawSmiley(depth)
        }
    }
}
