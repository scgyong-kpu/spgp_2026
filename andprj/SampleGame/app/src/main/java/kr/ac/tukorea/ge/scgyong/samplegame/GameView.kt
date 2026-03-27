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
import android.view.View
import androidx.core.graphics.withMatrix
import kotlin.math.max

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

    // 축약형 표현으로 Runnable 객체를 만들어 보자.
    private val updateRunnable = Runnable {
        update()
        invalidate()
        scheduleUpdate() // update() 가 끝난 뒤에 다음 업데이트를 예약한다.
    }

    // init block 보다 updateRunnable 선언이 먼저 되어야 null 이 아니게 된다.
    init {
        // 게임 화면이 만들어질 때, 일정 간격으로 update() 가 호출되도록 예약한다.
        scheduleUpdate()
    }

    var lastUpdateTime: Long = 0
    fun scheduleUpdate() {
        val now = System.currentTimeMillis()
        val elapsedSinceLastUpdate: Long = now - lastUpdateTime
        val targetDelay = (1000 / 60).toLong() // 60fps 라면 한 프레임 목표 시간은 약 16ms 이다.
        // 이전 update 이후 이미 시간이 많이 지났다면 delay 는 0 에 가까워지고,
        // 아직 덜 지났다면 남은 시간만큼 더 기다렸다가 다음 update 를 실행한다.
        val delay = max(0, targetDelay - elapsedSinceLastUpdate)

        postDelayed(updateRunnable, delay)
        lastUpdateTime = now // 이번 예약 시각을 다음 delay 계산의 기준으로 남긴다.
    }

    fun update() {
        ballRect.offset(1f, 2f)
        Log.d(javaClass.simpleName, "ballRect: $ballRect")
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
