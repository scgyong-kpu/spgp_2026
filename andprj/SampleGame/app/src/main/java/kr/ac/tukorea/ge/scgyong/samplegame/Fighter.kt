package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val FIGHTER_X = 450f
private const val FIGHTER_Y = 1200f
private const val FIGHTER_SIZE = 250f
private const val FIGHTER_ANGLE_OFFSET = 90f
private const val FIGHTER_SPEED = 500f

class Fighter(gctx: GameContext) {
    private val rect = RectF()
    private var x = 0f
    private var y = 0f
    private var angleDegree = -FIGHTER_ANGLE_OFFSET
    private var targetX = FIGHTER_X
    private var targetY = FIGHTER_Y

    init {
        setPosition(FIGHTER_X, FIGHTER_Y, appliesAngle = false)
    }

    private val bitmap = gctx.getBitmapResource(R.mipmap.plane_240)

    fun setTarget(x: Float, y: Float) {
        targetX = x
        targetY = y

        val dx = (targetX - this.x).toDouble()
        val dy = (targetY - this.y).toDouble()
        angleDegree = Math.toDegrees(atan2(dy, dx)).toFloat()
        Log.d(javaClass.simpleName, "angleDegree: ${"%.2f".format(angleDegree)}")
    }

    private fun setPosition(x: Float, y: Float, appliesAngle: Boolean = true) {
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

    fun update(gctx: GameContext) {
        val dx = targetX - x
        val dy = targetY - y
        val distance = sqrt(dx * dx + dy * dy)
        if (distance == 0f) return

        val step = FIGHTER_SPEED * gctx.frameTime
        if (distance <= step) {
            setPosition(targetX, targetY, appliesAngle = false)
            return
        }

        val radian = Math.toRadians(angleDegree.toDouble())
        setPosition(
            x + (cos(radian) * step).toFloat(),
            y + (sin(radian) * step).toFloat(),
            appliesAngle = false,
        )
    }

    fun draw(canvas: Canvas) {
        canvas.save()
        // 오른쪽을 바라보는 이미지였다면 atan2 각도를 그대로 써도 되지만,
        // 현재 plane_240 은 위를 향한 이미지이므로 90도 보정을 더해 준다.
        // 초기 angleDegree 도 -90도에서 시작해, 정지 상태일 때 위를 향한 기본 방향과 맞춘다.
        canvas.rotate(angleDegree + FIGHTER_ANGLE_OFFSET, x, y)
        canvas.drawBitmap(bitmap, null, rect, null)
        canvas.restore()
    }
}
