package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF

private const val VIRTUAL_WIDTH = 900f
private const val VIRTUAL_HEIGHT = 1600f

class GameMetrics() {
    val width = VIRTUAL_WIDTH
    val height = VIRTUAL_HEIGHT
    val transformMatrix = Matrix()
    val inverseTransformMatrix = Matrix()
    private val touchPoint = floatArrayOf(0f, 0f)
    private val sharedPointForReturn = PointF()

    fun onSize(w: Int, h: Int) {
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
    fun fromScreen(x: Float, y: Float): PointF {
        touchPoint[0] = x
        touchPoint[1] = y
        inverseTransformMatrix.mapPoints(touchPoint)
        sharedPointForReturn.set(touchPoint[0], touchPoint[1])
        return sharedPointForReturn
    }
    fun toScreen(x: Float, y: Float): PointF {
        touchPoint[0] = x
        touchPoint[1] = y
        transformMatrix.mapPoints(touchPoint)
        sharedPointForReturn.set(touchPoint[0], touchPoint[1])
        return sharedPointForReturn
    }
}

class GameContext(
    val view: GameView,
    var frameTime: Float = 0f, // 이전 프레임과의 시간 간격을 초 단위로 저장하는 변수이다.
    var currentTimeNanos: Long = 0L, // doFrame() 에서 전달된 nanos 를 저장하는 변수이다.
) {
    val metrics = GameMetrics()
    fun getBitmapResource(id: Int) = BitmapFactory.decodeResource(view.resources, id)
}
