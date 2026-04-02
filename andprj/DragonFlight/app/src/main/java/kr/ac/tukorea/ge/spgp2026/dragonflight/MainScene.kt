package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {
    // 예전처럼 0, 1 같은 Int 로 layer 를 구분해도 동작은 한다.
    // 하지만 enum 을 쓰면 "이 숫자가 무슨 layer 였지?"를 외우지 않아도 되고,
    // 나중에 BULLET, ENEMY, EFFECT 같은 layer 를 더 추가할 때도 코드가 더 읽기 쉬워진다.
    enum class Layer {
        PLAYER,
        BULLET,
    }

    // 이제는 JoyStick 같은 별도 입력 오브젝트를 두지 않고,
    // Player 가 직접 터치 방향을 해석해 좌/우 이동 방향을 결정한다.
    val player = Player(gctx)

    // World(arrayOf(...)) 에 넘기는 layer 순서가 곧 update / draw 순서가 된다.
    // 아직 Bullet 을 실제로 발사하지는 않지만, 다음 단계에서 바로 add() 할 수 있도록
    // BULLET layer 자리도 미리 잡아 둔다.
    override val world = World(arrayOf(Layer.PLAYER, Layer.BULLET)).apply {
        add(player, Layer.PLAYER)
    }

    // 현재 Scene 에서는 터치 입력을 따로 나누지 않고
    // 그대로 Player 에게 넘겨 Player 가 좌/우 방향을 직접 해석하게 한다.
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return player.onTouchEvent(event)
    }
}
