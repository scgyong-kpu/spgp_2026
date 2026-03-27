package kr.ac.tukorea.ge.scgyong.samplegame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.Choreographer
import android.view.View
import androidx.core.graphics.withMatrix

// 게임 내부 공간으로 사용할 가상 좌표계의 크기이다.
// 실제 화면 크기와는 별개로 게임 안에서는 900 x 1600 공간이 있다고 생각하고 그린다.
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr), Choreographer.FrameCallback {

    private val balls = arrayOf(
        Ball.random(context),
        Ball.random(context),
    )

    init {
        Choreographer.getInstance().postFrameCallback(this)
    }

    private val transformMatrix = Matrix()
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val scaleX = w / VIRTUAL_WIDTH
        val scaleY = h / VIRTUAL_HEIGHT
        val scale = minOf(scaleX, scaleY) // 잘리지 않게 더 작은 배율을 고른다.
        val contentWidth = VIRTUAL_WIDTH * scale
        val contentHeight = VIRTUAL_HEIGHT * scale
        val offsetX = (w - contentWidth) / 2f
        val offsetY = (h - contentHeight) / 2f
        transformMatrix.reset()
        transformMatrix.postTranslate(offsetX, offsetY) // 먼저 가운데로 옮긴다.
        transformMatrix.postScale(scale, scale, offsetX, offsetY) // 그 위치를 기준으로 확대/축소한다.
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withMatrix(transformMatrix) {
            drawDebugGrid() // 가상 좌표계의 격자선을 그린다.
            for (ball in balls) {
                ball.draw(this)
            }
        }
    }

    override fun doFrame(nanos: Long) {
        update()
        invalidate()
        // Choreographer 는 화면이 다시 그려지는 시점에 콜백을 호출해준다.
        // 그래서 화면이 60fps 라면 1초에 60번 update() 가 호출된다.
        // View LifeCycle 과 관계 없이 동작하므로, 보이는 동안에만 다음 업데이트를 예약해야 한다.
        if (isShown) {
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun update() {
        for ((index, ball) in balls.withIndex()) {
            ball.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
            Log.d(javaClass.simpleName, "ball[$index]=${ball.debugString()}")
        }
    }

    // 가상 좌표계가 실제로 어떤 범위와 간격을 가지는지 눈으로 확인하려고 그리는 디버그 격자이다.
    private fun Canvas.drawDebugGrid() {
        drawRect(borderRect, borderPaint) // 900 x 1600 가상 좌표계의 경계
        val step = 100f

        // 세로 격자선: x 값을 100씩 늘리며 위에서 아래로 선을 긋는다.
        var x = 0f
        while (x <= VIRTUAL_WIDTH) {
            drawLine(x, 0f, x, VIRTUAL_HEIGHT, gridPaint)
            x += step
        }

        // 가로 격자선: y 값을 100씩 늘리며 왼쪽에서 오른쪽으로 선을 긋는다.
        var y = 0f
        while (y <= VIRTUAL_HEIGHT) {
            drawLine(0f, y, VIRTUAL_WIDTH, y, gridPaint)
            y += step
        }
    }

    private val borderRect by lazy { RectF(0f, 0f, VIRTUAL_WIDTH, VIRTUAL_HEIGHT) }
    private val borderPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE // 테두리만 그린다.
            color = Color.RED
            strokeWidth = 10f
        }
    }
    private val gridPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE // 테두리만 그린다.
            color = Color.GRAY
            strokeWidth = 1f
        }
    }

    companion object {
        const val VIRTUAL_WIDTH = 900f
        const val VIRTUAL_HEIGHT = 1600f
    }
}
