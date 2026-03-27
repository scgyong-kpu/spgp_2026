package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import kotlin.math.atan2

private const val FIGHTER_X = 450f
private const val FIGHTER_Y = 1200f
private const val FIGHTER_SIZE = 250f
private const val FIGHTER_ANGLE_OFFSET = 90f

class Fighter(gctx: GameContext) {
    private val rect = RectF()
    private var x = 0f
    private var y = 0f
    private var angleDegree = -FIGHTER_ANGLE_OFFSET

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
        canvas.save()
        // plane_240 이미지의 기본 정면 방향과 atan2 로 계산한 각도 기준이 다를 수 있으므로,
        // 보정 각도(FIGHTER_ANGLE_OFFSET)를 더해 기대한 방향으로 맞춘다.
        canvas.rotate(angleDegree + FIGHTER_ANGLE_OFFSET, x, y)
        canvas.drawBitmap(bitmap, null, rect, null)
        canvas.restore()
    }
}
