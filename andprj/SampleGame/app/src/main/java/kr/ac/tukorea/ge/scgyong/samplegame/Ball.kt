package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.RectF

class Ball(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    var dx: Float,
    var dy: Float,
) {
    val rect = RectF(left, top, right, bottom)

    fun update(worldWidth: Float, worldHeight: Float) {
        rect.offset(dx, dy)

        if (rect.left < 0f || rect.right > worldWidth) {
            dx = -dx
            rect.offset(dx, 0f)
        }

        if (rect.top < 0f || rect.bottom > worldHeight) {
            dy = -dy
            rect.offset(0f, dy)
        }
    }
}
