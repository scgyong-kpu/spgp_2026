package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import kotlin.math.atan2

private const val FIGHTER_X = 450f
private const val FIGHTER_Y = 1200f
private const val FIGHTER_SIZE = 250f

class Fighter(gctx: GameContext) {
    private val rect = RectF()
    private var x = 0f
    private var y = 0f
    private var angleDegree = 0f

    init {
        setPosition(FIGHTER_X, FIGHTER_Y, appliesAngle = false)
    }

    private val bitmap = gctx.getBitmapResource(R.mipmap.plane_240)

    fun setPosition(x: Float, y: Float, appliesAngle: Boolean = true) {
        rect.set(
            x - FIGHTER_SIZE / 2f,
            y - FIGHTER_SIZE / 2f,
            x + FIGHTER_SIZE / 2f,
            y + FIGHTER_SIZE / 2f,
        )
        if (appliesAngle) {
            // 이전 위치에서 새 위치로 향하는 방향을 각도로 바꿔, 나중에 전투기 회전에 쓸 수 있게 둔다.
            val dx = (x - this.x).toDouble()
            val dy = (y - this.y).toDouble()
            angleDegree = Math.toDegrees(atan2(dy, dx)).toFloat()
            Log.d(javaClass.simpleName, "angleDegree: ${"%.2f".format(angleDegree)}")
        }
        this.x = x
        this.y = y
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, null, rect, null)
    }
}
