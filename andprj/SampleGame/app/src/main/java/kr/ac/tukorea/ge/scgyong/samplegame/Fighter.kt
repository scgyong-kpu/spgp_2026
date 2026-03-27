package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.BitmapFactory
import android.graphics.RectF

private const val FIGHTER_X = 450f
private const val FIGHTER_Y = 1200f
private const val FIGHTER_SIZE = 250f

class Fighter(gctx: GameContext) {
    private val rest = RectF(
        FIGHTER_X - FIGHTER_SIZE / 2f,
        FIGHTER_Y - FIGHTER_SIZE / 2f,
        FIGHTER_X + FIGHTER_SIZE / 2f,
        FIGHTER_Y + FIGHTER_SIZE / 2f,
    )

    private val bitmap = gctx.getBitmapResource(R.mipmap.plane_240)
    fun draw(canvas: android.graphics.Canvas) {
        canvas.drawBitmap(bitmap, null, rest, null)
    }
}
