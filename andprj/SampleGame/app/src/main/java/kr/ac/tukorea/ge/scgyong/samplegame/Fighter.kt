package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF

private const val FIGHTER_X = 450f
private const val FIGHTER_Y = 1200f
private const val FIGHTER_SIZE = 250f

class Fighter(gctx: GameContext) {
    private val rect = RectF(
        FIGHTER_X - FIGHTER_SIZE / 2f,
        FIGHTER_Y - FIGHTER_SIZE / 2f,
        FIGHTER_X + FIGHTER_SIZE / 2f,
        FIGHTER_Y + FIGHTER_SIZE / 2f,
    )

    private val bitmap = gctx.getBitmapResource(R.mipmap.plane_240)

    fun setPosition(x: Float, y: Float) {
        rect.set(
            x - FIGHTER_SIZE / 2f,
            y - FIGHTER_SIZE / 2f,
            x + FIGHTER_SIZE / 2f,
            y + FIGHTER_SIZE / 2f,
        )
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, null, rect, null)
    }
}
