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
import kotlin.math.roundToInt
import kotlin.math.sin

private const val FIGHTER_SIZE = 250f
private const val FIGHTER_ANGLE_OFFSET = 90f
private const val FIGHTER_SPEED = 500f
// 8방향은 360도를 8등분한 것이므로 한 칸 크기는 45도, 즉 PI / 4 radian 이다.
// 아래 update() 에서는 이 값을 기준으로 JoyStick 의 연속 angle 을 가까운 8방향으로 반올림한다.
private const val DIRECTION_8_STEP = (Math.PI / 4.0).toFloat()

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

        // 8방향 입력만 쓰는 전투기에서는 JoyStick 의 연속 angle 을 그대로 쓰지 않고,
        // 45도 단위로 반올림한 snappedAngle 을 사용한다.
        //
        // 같은 방식으로 방향 수를 바꾸고 싶다면 step 값만 바꾸면 된다.
        // 예를 들어:
        // - 4방향  : step = PI / 2
        // - 6방향  : step = PI / 3
        // - 16방향 : step = PI / 8
        //
        // 일반식으로 쓰면:
        //   val directionCount = 8
        //   val step = (2f * Math.PI.toFloat()) / directionCount
        //
        // "각도 값" 대신 "몇 번째 방향 칸인가"를 정수로 얻고 싶다면,
        // 아래처럼 roundToInt() 결과를 directionIndex 로 따로 저장해서 쓸 수 있다.
        //   val directionIndex = (joyStick.angle / step).roundToInt()
        //
        // 이 directionIndex 를 쓰면:
        // - sprite sheet 의 몇 번째 방향 프레임을 고를지 정하거나
        // - 0~7, 0~5 같은 정수 방향값을 AI / 상태머신에 넘기거나
        // - 0:오른쪽, 1:오른쪽아래 ... 같은 enum 과 연결하는 식의 응용이 가능하다.
        val snappedAngle = (joyStick.angle / DIRECTION_8_STEP).roundToInt() * DIRECTION_8_STEP
        val dx = cos(snappedAngle) * distance
        val dy = sin(snappedAngle) * distance
        val edgeMargin = FIGHTER_SIZE / 4f
        // 전투기 중심점이 화면 경계보다 일정 거리만큼 더 바깥으로 나갈 수 있게 둔다.
        // 이렇게 하면 스프라이트가 화면 가장자리에 걸칠 때도 이동이 덜 답답하게 느껴진다.
        x = max(-edgeMargin, min(gctx.metrics.width + edgeMargin, x + dx))
        y = max(-edgeMargin, min(gctx.metrics.height + edgeMargin, y + dy))
        angleDegree = Math.toDegrees(snappedAngle.toDouble()).toFloat()
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
