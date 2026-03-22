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

        // 실제 View 크기를 0.0~1.0 정규화 좌표계로 바꿔 놓고 smiley 를 그린다.
        canvas.save()
        canvas.translate(baseTranslateX, baseTranslateY)
        canvas.scale(baseScale, baseScale)
        drawSmiley(canvas)
        canvas.restore()
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

    private fun drawSmiley(canvas: Canvas, depth: Int = 3) {
        Log.d(javaClass.simpleName, "drawSmiley: depth=$depth")
        // 이 함수는 정규화 좌표계를 기준으로 그린다.
        // 따라서 얼굴은 항상 중심 (0.5, 0.5), 반지름 0.5 인 원으로 표현된다.
        val cx = 0.5f
        val cy = 0.5f
        val radius = 0.5f

        val eyeRadius = radius / 4f
        val leftEyeCx = cx - radius / 3f
        val rightEyeCx = cx + radius / 3f
        val eyeCy = cy - radius / 4f

        canvas.drawCircle(cx, cy, radius, fillPaint)
        canvas.drawCircle(cx, cy, radius, strokePaint)

        // depth 가 1보다 크면 눈 자리에 좌표계를 옮기고 축소한 뒤
        // 더 작은 smiley 를 다시 그린다.
        // 1이면 재귀를 멈추고 눈을 단순한 원으로만 그린다.
        if (depth > 1) {
            canvas.save()
            canvas.translate(leftEyeCx - eyeRadius, eyeCy - eyeRadius)
            canvas.scale(eyeRadius * 2f, eyeRadius * 2f)
            drawSmiley(canvas, depth - 1)
            canvas.restore()

            canvas.save()
            canvas.translate(rightEyeCx - eyeRadius, eyeCy - eyeRadius)
            canvas.scale(eyeRadius * 2f, eyeRadius * 2f)
            drawSmiley(canvas, depth - 1)
            canvas.restore()
        } else {
            canvas.drawCircle(leftEyeCx, eyeCy, eyeRadius, strokePaint)
            canvas.drawCircle(rightEyeCx, eyeCy, eyeRadius, strokePaint)
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
        canvas.drawArc(mouthLeft, eyeCy, mouthRight, mouthBottom, 15f, 150f, false, strokePaint)
    }
}
