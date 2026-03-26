package kr.ac.tukorea.ge.scgyong.samplegame

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix

// 게임 내부 공간으로 사용할 가상 좌표계의 크기이다.
// 실제 화면 크기와는 별개로 게임 안에서는 900 x 1600 공간이 있다고 생각하고 그린다.
private const val VIRTUAL_WIDTH = 900f
private const val VIRTUAL_HEIGHT = 1600f

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val ballRect = RectF(350f, 700f, 550f, 900f)
    private val ballBitmap = BitmapFactory.decodeResource(resources, R.mipmap.soccer_ball_240)

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
            // drawDebugGrid 가 Canvas extension 이라서, 이 블록 안에서는 drawDebugGrid() 만으로 바로 쓸 수 있다.
            drawDebugGrid() // 가상 좌표계의 격자선을 그린다.
            drawBitmap(ballBitmap, null, ballRect, null) // 공의 위치와 크기는 ballRect 로 정한다.
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            move()
        }
        return super.onTouchEvent(event)
    }

    private fun move() {
        // 공이 오른쪽으로 100씩 이동하도록 해 본다.
        ballRect.left += 100f
        Log.d(javaClass.simpleName, "ballRect: $ballRect")
        // 공은 움직이지 않는다.
        // 그렇다면 move() 가 호출되지 않는 것인지?
        // move() 는 호출되는데 공이 움직이지 않는 것인지?
        // 좌표 이동 관련 다른 문제는 없는지?
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

}
