package kr.ac.tukorea.ge.spgp2026.a2dg.view

import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log

private const val DEFAULT_VIRTUAL_WIDTH = 900f
private const val DEFAULT_VIRTUAL_HEIGHT = 1600f

class GameMetrics {
    // 게임 안에서 공통으로 쓰는 가상 좌표계 크기이다.
    // 기본값은 예전처럼 900 x 1600 이지만,
    // 특정 게임이 다른 내부 좌표계를 쓰고 싶다면 createRootScene() 같은 초기화 시점에
    // setSize() 를 호출해 원하는 값으로 바꿀 수 있게 한다.
    var width = DEFAULT_VIRTUAL_WIDTH
        private set
    var height = DEFAULT_VIRTUAL_HEIGHT
        private set

    // 실제 화면에 그릴 때는 transformMatrix 를,
    // 터치 입력을 가상 좌표계로 되돌릴 때는 inverseTransformMatrix 를 사용한다.
    val transformMatrix = Matrix()
    val inverseTransformMatrix = Matrix()

    // 실제 화면의 사각형이 현재 가상 좌표계에서 어떤 범위로 보이는지를 저장한다.
    // 예를 들어 좌우 또는 상하에 letterbox 여백이 생기면,
    // 이 값은 단순히 (0,0,width,height) 와 같지 않을 수 있다.
    val screenRect = RectF()

    // mapPoints() 는 배열을 받는 API 이므로 입력 좌표 변환에 쓸 작은 버퍼를 미리 만들어 재사용한다.
    private val touchPoint = floatArrayOf(0f, 0f)
    // fromScreen(), toScreen() 이 PointF 를 매번 새로 만들지 않도록 반환용 객체도 하나만 재사용한다.
    private val sharedPointForReturn = PointF()

    fun setSize(width: Float, height: Float) {
        this.width = width
        this.height = height
    }

    fun onSize(w: Int, h: Int) {
        val scaleX = w / width
        val scaleY = h / height
        val scale = minOf(scaleX, scaleY) // 찌그러지지 않게 더 작은 배율을 고른다.
        val contentWidth = width * scale
        val contentHeight = height * scale
        val offsetX = (w - contentWidth) / 2f
        val offsetY = (h - contentHeight) / 2f
        transformMatrix.reset()
        transformMatrix.postTranslate(offsetX, offsetY) // 먼저 가운데로 옮긴다.
        transformMatrix.postScale(scale, scale, offsetX, offsetY) // 그 위치를 기준으로 확대/축소한다.
        transformMatrix.invert(inverseTransformMatrix) // 그리기용 변환이 정해지면 입력용 역변환도 함께 계산한다.

        screenRect.set(0f, 0f, w.toFloat(), h.toFloat())
        inverseTransformMatrix.mapRect(screenRect)
        Log.d(
            javaClass.simpleName,
            "onSize: screen=${w}x$h, virtual=${width}x$height, screenRect=$screenRect"
        )
    }

    // 실제 화면 좌표를 가상 좌표계 좌표로 되돌린다.
    fun fromScreen(x: Float, y: Float): PointF {
        touchPoint[0] = x
        touchPoint[1] = y
        inverseTransformMatrix.mapPoints(touchPoint)
        sharedPointForReturn.set(touchPoint[0], touchPoint[1])
        return sharedPointForReturn
    }

    // 가상 좌표계 좌표를 실제 화면 좌표로 바꾼다.
    fun toScreen(x: Float, y: Float): PointF {
        touchPoint[0] = x
        touchPoint[1] = y
        transformMatrix.mapPoints(touchPoint)
        sharedPointForReturn.set(touchPoint[0], touchPoint[1])
        return sharedPointForReturn
    }
}
