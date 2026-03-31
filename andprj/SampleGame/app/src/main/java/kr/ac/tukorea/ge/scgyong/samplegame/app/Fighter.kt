package kr.ac.tukorea.ge.scgyong.samplegame.app

import android.graphics.Canvas
import android.util.Log
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.JoyStick
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.scgyong.samplegame.R
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

private const val FIGHTER_SIZE = 250f
private const val FIGHTER_ANGLE_OFFSET = 90f
private const val FIGHTER_SPEED = 500f
private const val BULLET_INTERVAL = 0.2f

class Fighter(gctx: GameContext, private val joyStick: JoyStick) : Sprite(gctx, R.mipmap.plane_240) {
    private var angleDegree = -FIGHTER_ANGLE_OFFSET
    private var bulletCoolTime = 0f

    init {
        width = FIGHTER_SIZE
        height = FIGHTER_SIZE
        // x, y 는 스프라이트 중심점이므로, 아래쪽에 보이게 하려면
        // 높이 절반만큼은 화면 안쪽으로 들어오게 두어야 한다.
        x = gctx.metrics.width / 2f
        y = gctx.metrics.height - FIGHTER_SIZE / 2f
    }

    override fun update(gctx: GameContext) {
        bulletCoolTime -= gctx.frameTime
        if (bulletCoolTime <= 0f) {
            val bulletAngle = Math.toRadians(angleDegree.toDouble()).toFloat()
            val bullet = Bullet(x, y, bulletAngle)
            // Bullet 생성 요청은 현재 Scene 의 World 에 보낸다.
            // 실제 add() 를 바로 반영할지, 나중에 반영할지는 World 가 결정한다.
            (gctx.scene as? MainScene)?.world?.add(bullet, MainScene.Layer.BULLET)
            bulletCoolTime = BULLET_INTERVAL
        }

        // JoyStick 의 angle 은 radian 값으로, power 는 0.0~1.0 세기 값으로 사용한다.
        // 따라서 조이스틱을 조금만 밀면 천천히 움직이고, 끝까지 밀면 최대 속도로 움직인다.
        if (joyStick.power == 0f) {
            return
        }
        val moveAngle = joyStick.angle
        val distance = FIGHTER_SPEED * joyStick.power * gctx.frameTime
        val dx = cos(moveAngle) * distance
        val dy = sin(moveAngle) * distance
        val edgeMargin = FIGHTER_SIZE / 4f
        // 전투기 중심점이 화면 경계보다 일정 거리만큼 더 바깥으로 나갈 수 있게 둔다.
        // 이렇게 하면 스프라이트가 화면 가장자리에 걸칠 때도 이동이 덜 답답하게 느껴진다.
        x = max(-edgeMargin, min(gctx.metrics.width + edgeMargin, x + dx))
        y = max(-edgeMargin, min(gctx.metrics.height + edgeMargin, y + dy))
        angleDegree = Math.toDegrees(moveAngle.toDouble()).toFloat()
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
