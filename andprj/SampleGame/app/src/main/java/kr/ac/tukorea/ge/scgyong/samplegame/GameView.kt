package kr.ac.tukorea.ge.scgyong.samplegame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix

private const val VIRTUAL_WIDTH = 900f
private const val VIRTUAL_HEIGHT = 1600f

// 게임 내부 공간으로 사용할 가상 좌표계의 크기이다.
// 실제 화면 크기와는 별개로 게임 안에서는 900 x 1600 공간이 있다고 생각하고 그린다.
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr), Choreographer.FrameCallback {

    private val gctx = GameContext(this, VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    private val gameObjects = buildList<IGameObject> {
        repeat(10) { add(Ball.random(gctx)) }
        repeat(5) { add(BouncingCircle(gctx)) }
        add(Fighter(gctx))
    }.toTypedArray()
    private val fighter: Fighter
        get() = gameObjects.last() as Fighter

    init {
        Choreographer.getInstance().postFrameCallback(this)
    }

    private val transformMatrix = Matrix()
    private val inverseTransformMatrix = Matrix()
    private val touchPoint = floatArrayOf(0f, 0f)

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
        transformMatrix.invert(inverseTransformMatrix) // 그리기용 변환이 정해질 때 입력용 역변환도 함께 계산해 둔다.
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withMatrix(transformMatrix) {
            drawDebugGrid() // 가상 좌표계의 격자선을 그린다.
            for (gameObject in gameObjects) {
                gameObject.draw(this)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // event.x, event.y 는 실제 화면 좌표이므로, 역행렬을 적용해 가상 좌표계 값으로 되돌린다.
                touchPoint[0] = event.x
                touchPoint[1] = event.y
                inverseTransformMatrix.mapPoints(touchPoint)
                fighter.setTarget(touchPoint[0], touchPoint[1])
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

    fun update() {
        for (gameObject in gameObjects) {
            gameObject.update(gctx)
        }
    }

    // 가상 좌표계가 실제로 어떤 범위와 간격을 가지는지 눈으로 확인하려고 그리는 디버그 격자이다.
    private fun Canvas.drawDebugGrid() {
        drawRect(borderRect, borderPaint) // 900 x 1600 가상 좌표계의 경계
        val step = 100f

        // 세로 격자선은 x 값을 100씩 늘리며 위에서 아래로 선을 긋는다.
        var x = 0f
        while (x <= VIRTUAL_WIDTH) {
            drawLine(x, 0f, x, VIRTUAL_HEIGHT, gridPaint)
            x += step
        }

        // 가로 격자선은 y 값을 100씩 늘리며 왼쪽에서 오른쪽으로 선을 긋는다.
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
