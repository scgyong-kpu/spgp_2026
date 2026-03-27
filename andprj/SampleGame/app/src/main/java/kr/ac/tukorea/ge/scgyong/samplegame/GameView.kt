package kr.ac.tukorea.ge.scgyong.samplegame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import kotlin.math.roundToInt


// 게임 내부 공간으로 사용할 가상 좌표계의 크기이다.
// 실제 화면 크기와는 별개로 게임 안에서는 900 x 1600 공간이 있다고 생각하고 그린다.
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr), Choreographer.FrameCallback {

    private val gctx = GameContext(this)

    // Fighter 는 onTouchEvent 에서 setTarget() 으로 직접 접근해야 하므로, gameObjects 안에만 숨기기보다
    // 멤버로 하나 들고 있는 편이 더 단순하고 읽기 쉽다.
    private val fighter = Fighter(gctx)
    private val gameObjects = buildList<IGameObject> {
        repeat(10) { add(Ball.random(gctx)) }
        repeat(5) { add(BouncingCircle(gctx)) }
        add(fighter)
    }.toTypedArray()

    init {
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun update() {
        for (gameObject in gameObjects) {
            gameObject.update(gctx)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withMatrix(gctx.metrics.transformMatrix) {
            if (BuildConfig.DRAWS_DEBUG_GRID) {
                drawDebugGrid() // 가상 좌표계의 격자선을 그린다.
            }
            for (gameObject in gameObjects) {
                gameObject.draw(this)
            }
            if (BuildConfig.DRAWS_DEBUG_INFO || BuildConfig.DRAWS_FPS_GRAPH) {
                drawDebugInfo() // FPS 등의 디버그 정보를 그린다.
            }
        }
    }

    private val debugFrames by lazy { DebugFrames() }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        gctx.metrics.onSize(w, h)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // event.x, event.y 는 실제 화면 좌표이므로, 역행렬을 적용해 가상 좌표계 값으로 되돌린다.
                val pt = gctx.metrics.fromScreen(event.x, event.y)
                fighter.setTarget(pt.x, pt.y)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // doFrame() 에게 전달된 nanos 간의 차이를 계산하여 frameTime 을 계산해 둔다.
    // doFrame() 이 최초 호출 된 시점에는 previousNanos 가 0 이어서
    // 매우 큰 frameTime 이 생성되므로 0 일때에는 하면 안 된다.
    override fun doFrame(nanos: Long) {
        val previousNanos = gctx.currentTimeNanos
        gctx.currentTimeNanos = nanos
        if (previousNanos != 0L) {
            gctx.frameTime = (nanos - previousNanos) / 1_000_000_000f
            //Log.d(javaClass.simpleName, "frameTime=${(gctx.frameTime / (1/60f)).roundToInt()} frame")
            update()
            invalidate()
        }
        if (isShown) {
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    private fun Canvas.drawDebugInfo() {
        if (BuildConfig.DRAWS_DEBUG_INFO) {
            val text = "FPS: ${"%.1f".format(1 / gctx.frameTime)}"
            drawText(text, 20f, 60f, debugPaint)
        }
        if (BuildConfig.DRAWS_FPS_GRAPH) {
            debugFrames.add((gctx.frameTime / (1 / 60f)).roundToInt().toFloat())
            debugFrames.draw(this)
        }
    }
    // 가상 좌표계가 실제로 어떤 범위와 간격을 가지는지 눈으로 확인하려고 그리는 디버그 격자이다.
    private fun Canvas.drawDebugGrid() {
        drawRect(borderRect, borderPaint) // 900 x 1600 가상 좌표계의 경계
        val step = 100f

        // 세로 격자선은 x 값을 100씩 늘리며 위에서 아래로 선을 긋는다.
        var x = 0f
        while (x <= gctx.metrics.width) {
            drawLine(x, 0f, x, gctx.metrics.height, gridPaint)
            x += step
        }

        // 가로 격자선은 y 값을 100씩 늘리며 왼쪽에서 오른쪽으로 선을 긋는다.
        var y = 0f
        while (y <= gctx.metrics.height) {
            drawLine(0f, y, gctx.metrics.width, y, gridPaint)
            y += step
        }
    }

    private val borderRect by lazy { RectF(0f, 0f, gctx.metrics.width, gctx.metrics.height) }
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
    private val debugPaint by lazy {
        Paint().apply {
            color = Color.BLUE
            textSize = 50f
        }
    }
}

private class DebugFrames(capacity: Int = 150) {
    private val values = FloatArray(capacity)
    private var start = 0
    private var count = 0
    private val path = Path()
    private val paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    fun add(frameUnits: Float) {
        val end = (start + count) % values.size
        values[end] = frameUnits
        if (count < values.size) {
            count++
        } else {
            start = (start + 1) % values.size
        }
    }

    fun draw(canvas: Canvas) {
        if (count == 0) return

        val graphX = 20f
        val graphMinY = 100f
        val graphWidth = 860f
        val graphHeight = 120f
        val maxFrameUnits = 6f
        val dx = if (values.size > 1) graphWidth / (values.size - 1) else 0f

        path.reset()
        var previousX = 0f
        var previousY = 0f
        for (i in 0 until count) {
            val index = (start + i) % values.size
            val clamped = values[index].coerceIn(0f, maxFrameUnits)
            val x = graphX + dx * i
            val y = graphMinY + (clamped / maxFrameUnits) * graphHeight
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                if (y != previousY) {
                    path.lineTo(previousX, previousY)
                    path.lineTo(x, y)
                }
            }
            previousX = x
            previousY = y
        }
        path.lineTo(previousX, previousY)
        canvas.drawPath(path, paint)
    }
}
