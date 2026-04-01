package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.JoyStick
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.cos

class Player(gctx: GameContext, val joystick: JoyStick) : Sprite(gctx, R.mipmap.fighter) {
    // 먼저 화면에 보이는 기본 크기와 시작 위치만 override 해 둔다.
    // 나중에 Player 가 화면 경계를 벗어나지 않게 하거나,
    // 기체별 크기를 다르게 둘 때도 같은 방식으로 값을 바꿀 수 있다.
    override var width = 144f
    override var height = 160f
    override var x = 450f
    override var y = 1400f

    override fun update(gctx: GameContext) {
        // Player 이동식의 핵심은 아래 한 줄이다.
        //
        //   SPEED * gctx.frameTime * joystick.power * cos(joystick.angle)
        //
        // 각 항의 의미를 풀어 쓰면:
        // - SPEED:
        //   "1초에 얼마나 빠르게 움직일지"를 나타내는 기본 속도이다.
        // - gctx.frameTime:
        //   지난 프레임과 이번 프레임 사이의 시간(초)이다.
        //   이 값을 곱해 주면 기기 성능이 달라도
        //   "1초 기준으로 비슷한 거리"를 움직이게 만들 수 있다.
        // - joystick.power:
        //   조이스틱 thumb 가 중심에서 얼마나 멀리 나갔는지를 0.0~1.0 으로 나타낸 값이다.
        //   0 이면 손을 떼었거나 거의 움직이지 않은 상태이고,
        //   1 에 가까울수록 끝까지 민 상태이므로 더 빠르게 이동한다.
        // - cos(joystick.angle):
        //   현재 조이스틱 방향 벡터를 x축 성분만 꺼낸 값이다.
        //   angle 이 0 이면 cos 값은 1 이라서 오른쪽으로 최대 이동하고,
        //   angle 이 PI 이면 -1 이라서 왼쪽으로 최대 이동한다.
        //   angle 이 위/아래 방향에 가까우면 cos 값이 0 에 가까워져
        //   x축 이동량도 자연스럽게 줄어든다.
        //
        // 즉 이 한 줄은
        // "기본 속도" * "프레임 시간" * "얼마나 세게 밀었는가" * "x축 방향 성분"
        // 을 계산해서 이번 프레임의 x 이동량으로 더하는 것이다.
        //
        // 나중에 2차원 이동으로 확장할 때는 y 쪽에도 같은 방식으로
        // sin(joystick.angle) 을 사용하면 된다.
        x += SPEED * gctx.frameTime * joystick.power * cos(joystick.angle)
    }

    companion object {
        const val SPEED = 300f
    }
}
