package kr.ac.tukorea.ge.scgyong.samplegame.app

import android.graphics.Canvas
import android.util.Log
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.Sprite
import kr.ac.tukorea.ge.scgyong.samplegame.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.cos
import kotlin.math.sin

private const val FIGHTER_SIZE = 250f
private const val FIGHTER_ANGLE_OFFSET = 90f
private const val FIGHTER_SPEED = 500f

class Fighter(gctx: GameContext, private val joyStick: JoyStick) : Sprite(gctx, R.mipmap.plane_240) {
    private var angleDegree = -FIGHTER_ANGLE_OFFSET

    init {
        width = FIGHTER_SIZE
        height = FIGHTER_SIZE
        // x, y 는 스프라이트 중심점이므로, 아래쪽에 보이게 하려면
        // 높이 절반만큼은 화면 안쪽으로 들어오게 두어야 한다.
        x = gctx.metrics.width / 2f
        y = gctx.metrics.height - FIGHTER_SIZE / 2f
    }

    override fun update(gctx: GameContext) {
        // 조이스틱의 angle 은 radian, power 는 0.0~1.0 이다.
        // power 가 0 이면 멈춰 있고, 1.0 이면 최대 속도로 움직인다.
        val distance = FIGHTER_SPEED * joyStick.power * gctx.frameTime
        if (distance == 0f) {
            return
        }

        val dx = cos(joyStick.angle) * distance
        val dy = sin(joyStick.angle) * distance
        val edgeMargin = FIGHTER_SIZE / 4f
        // 전투기 중심점이 화면 경계보다 일정 거리만큼 더 바깥으로 나갈 수 있게 둔다.
        // 이렇게 하면 스프라이트가 화면 가장자리에 걸칠 때도 이동이 덜 답답하게 느껴진다.
        x = max(-edgeMargin, min(gctx.metrics.width + edgeMargin, x + dx))
        y = max(-edgeMargin, min(gctx.metrics.height + edgeMargin, y + dy))
        angleDegree = Math.toDegrees(joyStick.angle.toDouble()).toFloat()
        Log.d(javaClass.simpleName, "angleDegree: ${"%.2f".format(angleDegree)} power=${"%.2f".format(joyStick.power)}")
    }

    override fun draw(canvas: Canvas) {
        canvas.withRotation(angleDegree + FIGHTER_ANGLE_OFFSET, x, y) {
            // save, rotate, restore 가 자동으로 일어나는 withRotation 블록 안에서 그리면,
            // 각도 만큼 회전된 상태로 그려진다. 실행 결과는 같고, 표현만 간결해 진다.
            syncDstRect()
            drawBitmap(bitmap, srcRect, dstRect, null)
        }
    }
}
