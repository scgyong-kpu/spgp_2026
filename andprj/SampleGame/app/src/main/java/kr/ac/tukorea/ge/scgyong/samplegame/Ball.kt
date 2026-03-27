package kr.ac.tukorea.ge.scgyong.samplegame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF

class Ball(
    context: Context,
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    var dx: Float,
    var dy: Float,
) {
    val rect = RectF(left, top, right, bottom)
    private val bitmap: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.mipmap.soccer_ball_240)

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

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    fun debugString(): String {
        return "rect=$rect, dx=$dx, dy=$dy"
    }
}
