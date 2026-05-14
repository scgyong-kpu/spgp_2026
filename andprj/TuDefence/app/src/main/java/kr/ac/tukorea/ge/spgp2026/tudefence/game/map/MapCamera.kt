package kr.ac.tukorea.ge.spgp2026.tudefence.game.map

import android.graphics.Matrix
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// MapCamera 는 map 좌표계를 game 화면 좌표계로 변환하는 상태를 가진다.
// screenX = (mapX - scrollX) * scale 이 되도록 matrix 를 만든다.
// 배경, 적, 포탑 같은 map 위 객체들이 같은 matrix 를 공유하면 확대/이동이 함께 적용된다.
class MapCamera(
    private val gctx: GameContext,
    private val mapWidth: Float,
    private val mapHeight: Float,
) {
    val matrix = Matrix()

    private var scrollX = 0f
    private var scrollY = 0f
    private var scale = 1f

    init {
        updateMatrix()
    }

    fun scrollBy(screenDx: Float, screenDy: Float) {
        scrollTo(scrollX + screenDx / scale, scrollY + screenDy / scale)
    }

    fun setScale(newScale: Float, focusX: Float, focusY: Float) {
        val oldScale = scale
        if (oldScale == newScale) return

        // focusX/focusY 는 화면에서 손가락이 가리키는 game 좌표이다.
        // 확대 전 손가락 아래에 있던 map 좌표가 확대 후에도 같은 자리에 남도록 scroll 을 보정한다.
        val focusMapX = scrollX + focusX / oldScale
        val focusMapY = scrollY + focusY / oldScale
        scale = newScale
        scrollTo(focusMapX - focusX / scale, focusMapY - focusY / scale)
    }

    private fun scrollTo(x: Float, y: Float) {
        val visibleWidth = gctx.metrics.width / scale
        val visibleHeight = gctx.metrics.height / scale
        scrollX = x.coerceIn(0f, maxOf(0f, mapWidth - visibleWidth))
        scrollY = y.coerceIn(0f, maxOf(0f, mapHeight - visibleHeight))
        updateMatrix()
    }

    private fun updateMatrix() {
        matrix.reset()
        matrix.setScale(scale, scale)
        matrix.postTranslate(-scrollX * scale, -scrollY * scale)
    }
}
