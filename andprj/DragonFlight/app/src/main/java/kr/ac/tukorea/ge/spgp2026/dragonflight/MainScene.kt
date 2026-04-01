package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.JoyStick
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {
    // 예전처럼 0, 1 같은 Int 로 layer 를 구분해도 동작은 한다.
    // 하지만 enum 을 쓰면 "이 숫자가 무슨 layer 였지?"를 외우지 않아도 되고,
    // 나중에 BULLET, ENEMY, EFFECT 같은 layer 를 더 추가할 때도 코드가 더 읽기 쉬워진다.
    enum class Layer {
        PLAYER,
        JOYSTICK,
    }

    // JoyStick 은 화면 오른쪽 아래에 붙여 둔다.
    // centerX, centerY 에 음수를 주면 a2dg JoyStick 이 각각 오른쪽, 아래쪽 기준 거리로 해석한다.
    // 즉 centerX = 150f 가 아니라 centerX = -150f 로 주면 "오른쪽에서 150 떨어진 위치"라는 뜻이 된다.
    val joystick = JoyStick(gctx,
        R.mipmap.tu_joystick_bg,
        R.mipmap.tu_joystick_thumb,
        centerX = 150f, centerY = -150f, bgRadius = 134.1f, thumbRadius = 87f)

    // Player 는 JoyStick 을 받아서 update() 때 angle, power 값을 읽어 움직인다.
    val player = Player(gctx, joystick)

    // World(arrayOf(...)) 에 넘기는 layer 순서가 곧 update / draw 순서가 된다.
    // 지금은 PLAYER 를 먼저, JOYSTICK 을 나중에 두어서
    // 게임 오브젝트를 먼저 그리고 조이스틱을 그 위에 덮어 그리게 한다.
    override val world = World(arrayOf(Layer.PLAYER, Layer.JOYSTICK)).apply {
        add(player, Layer.PLAYER)
        add(joystick, Layer.JOYSTICK)
    }

    // 현재 Scene 에서는 터치 입력을 별도로 해석하지 않고,
    // 그대로 JoyStick 에 전달해서 onTouchEvent() 처리만 맡긴다.
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return joystick.onTouchEvent(event)
    }
}
