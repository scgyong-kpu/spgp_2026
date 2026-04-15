package kr.ac.tukorea.ge.spgp2026.a2dg.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene

// GameView 는 GameMetrics 가 들고 있는 현재 가상 좌표계를 기준으로 장면을 그리고 입력을 처리한다.
// 따라서 게임이 createRootScene() 같은 시점에서 metrics.setSize() 를 호출하면,
// 그 이후에는 여기서도 그 새 가상 좌표계 크기를 기준으로 동작하게 된다.
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr), Choreographer.FrameCallback {

    // 프레임 시간, 리소스 접근, 화면 metrics, scene stack 같은 공통 게임 문맥을 한곳에 모아 둔다.
    private val gctx = GameContext(this)
    private var running = true

    // context 가 Activity 이면 바로 반환하고,
    // ContextThemeWrapper 같은 래퍼가 감싸고 있으면 체인을 따라가며 Activity 를 찾는다.
    private val activity: Activity?
        get() {
            var ctx = context
            while (ctx is ContextWrapper) {
                if (ctx is Activity) return ctx
                ctx = ctx.baseContext
            }
            return null
        }

    companion object {
        // GameView 가 직접 BuildConfig 를 읽지는 않지만,
        // 바깥쪽 app 코드가 이 값을 채워 넣어 디버그 표시 여부를 제어할 수 있게 한다.
        var drawsDebugGrid = true
        var drawsDebugInfo = true
        var drawsFpsGraph = true
    }

    init {
        Choreographer.getInstance().postFrameCallback(this)
    }

    // app 쪽은 "어떤 Scene 을 시작 장면으로 쓸지"만 factory 로 넘기고,
    // GameView 는 자신의 gctx 를 넘겨 실제 Scene 생성을 요청한다.
    // 이렇게 하면 a2dg 의 GameView 가 app 의 MainScene 을 직접 알 필요가 없다.
    fun setRootScene(factory: (GameContext) -> Scene) {
        gctx.sceneStack.push(factory(gctx))
    }

    fun update() {
        gctx.sceneStack.top?.update(gctx)
    }

    // 앱이 background 로 내려가는 동안에는 doFrame() 을 더 예약하지 않게 멈춘다.
    // 이때 현재 Scene 에도 onPause() 를 전달해 각 장면이 입력, 사운드 같은 부수 상태를 정리할 기회를 준다.
    fun pauseGame() {
        if (!running) return
        running = false
        gctx.sceneStack.top?.onPause()
    }

    // foreground 로 돌아올 때는 직전 nanos 를 0 으로 끊어 준다.
    // 그렇지 않으면 pause 되어 있던 시간 전체가 다음 doFrame() 의 frameTime 으로 잡혀
    // 총알, 적, 쿨타임이 한 프레임에 훅 진행된 것으로 보일 수 있다.
    fun resumeGame() {
        if (running) return
        running = true
        gctx.currentTimeNanos = 0L
        Choreographer.getInstance().postFrameCallback(this)
        gctx.sceneStack.top?.onResume()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 이 블록 안에서는 실제 화면 좌표가 아니라
        // 현재 GameMetrics 가 들고 있는 가상 좌표계 기준으로 그린다고 생각하면 된다.
        canvas.withMatrix(gctx.metrics.transformMatrix) {
            if (drawsDebugGrid) {
                drawDebugGrid() // 가상 좌표계의 격자선을 그린다.
            }
            gctx.sceneStack.top?.let { topScene ->
                if (topScene.clipsRect) {
                    canvas.clipRect(gctx.metrics.borderRect)
                }
                topScene.draw(this)
            }
            if (drawsDebugInfo || drawsFpsGraph) {
                drawDebugInfo() // FPS 등의 디버그 정보를 그린다.
            }
        }
    }

    private val debugFrames by lazy { DebugFrames(gctx) }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // View 실제 크기가 바뀌는 시점에 metrics 안의 transform / inverse transform 도 함께 다시 계산한다.
        gctx.metrics.onSize(w, h)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return (gctx.sceneStack.top?.onTouchEvent(event) ?: false) || super.onTouchEvent(event)
    }

    fun onBackPressed(): Boolean {
        return gctx.sceneStack.top?.onBackPressed() ?: false
    }

    // doFrame() 에 전달된 nanos 간의 차이를 계산하여 frameTime 을 계산해 둔다.
    // doFrame() 이 최초 호출 된 시점에는 previousNanos 가 0 이어서
    // 매우 큰 frameTime 이 생성되므로 0 일때에는 하면 안 된다.
    override fun doFrame(nanos: Long) {
        // pauseGame() 직전에 이미 예약돼 있던 callback 하나가 늦게 도착할 수 있으므로,
        // 멈춘 상태에서는 frameTime 계산이나 update 를 진행하지 않고 바로 무시한다.
        if (!running) return

        val previousNanos = gctx.currentTimeNanos
        gctx.currentTimeNanos = nanos
        if (previousNanos != 0L) {
            gctx.frameTime = (nanos - previousNanos) / 1_000_000_000f
            update()

            // Scene 의 update 나 touch 처리 도중 마지막 Scene 이 pop() 되어
            // stack 이 비었다면 더 이상 그릴 것이 없으므로 Activity 를 종료한다.
            if (gctx.sceneStack.top == null) {
                activity?.finish()
                return
            }

            invalidate()
        }
        if (running && isShown) {
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    private fun Canvas.drawDebugInfo() {
        if (drawsDebugInfo) {
            val fps = "%.1f".format(1 / gctx.frameTime)
            val world = gctx.sceneStack.top?.world
            val objectCount = world?.objectCount ?: 0
            val countsForLayers = world?.getDebugCounts() ?: "[]"
            drawText("$objectCount $countsForLayers", 20f, 50f, debugPaint)
            drawText("FPS: $fps", 20f, 105f, debugPaint)
        }
        if (drawsFpsGraph) {
            // 최근 프레임을 1/60 초 기준 몇 frame 이었는지로 바꿔 저장하면 그래프를 더 직관적으로 읽을 수 있다.
            debugFrames.add((gctx.frameTime / (1 / 60f)))
            debugFrames.draw(this)
        }
    }

    // 가상 좌표계가 실제로 어떤 범위와 간격을 가지는지 눈으로 확인하려고 그리는 디버그 격자이다.
    private fun Canvas.drawDebugGrid() {
        drawRect(gctx.metrics.borderRect, borderPaint) // 현재 가상 좌표계의 경계
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
            textSize = 40f
            typeface = Typeface.MONOSPACE
        }
    }
}

private class DebugFrames(val gctx: GameContext, capacity: Int = 150) {
    private val values = FloatArray(capacity)
    private var start = 0
    private var count = 0
    private val path = Path()
    private val paint = Paint().apply {
        color = Color.MAGENTA
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
        val graphMaxX = gctx.metrics.width - 20f
        val graphMinY = 100f
        val graphWidth = graphMaxX - graphX
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
