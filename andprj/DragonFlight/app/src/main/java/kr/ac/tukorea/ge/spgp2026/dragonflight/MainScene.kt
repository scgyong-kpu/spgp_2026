package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.enums.enumEntries

class MainScene(gctx: GameContext) : Scene(gctx) {
    // 예전처럼 0, 1 같은 Int 로 layer 를 구분해도 동작은 한다.
    // 하지만 enum 을 쓰면 "이 숫자가 무슨 layer 였지?"를 외우지 않아도 되고,
    // 나중에 BULLET, ENEMY, EFFECT 같은 layer 를 더 추가할 때도 코드가 더 읽기 쉬워진다.
    enum class Layer {
        PLAYER,
        BULLET,
        ENEMY,
    }

    // 이제는 JoyStick 같은 별도 입력 오브젝트를 두지 않고,
    // Player 가 직접 터치 방향을 해석해 좌/우 이동 방향을 결정한다.
    val player = Player(gctx)

    // layer 가 enum 이면 Layer.entries 로 enum 전체를 그대로 꺼낼 수 있다.
    // 그래서 arrayOf(Layer.PLAYER, ...) 를 일일이 다시 적지 않아도 된다.
    // ENEMY layer 는 이번 단계에서 touch down 으로 Enemy 를 하나 만들어 보고,
    // 다음 단계에서 화면 밖 삭제나 생성 규칙을 붙여 갈 자리이다.
    override val world = World(Layer.entries.toTypedArray()).apply {
        add(player, Layer.PLAYER)
    }

    // 현재 Scene 에서는 터치 입력을 따로 나누지 않고
    // 그대로 Player 에게 넘겨 Player 가 목표 위치를 직접 해석하게 한다.
    // 다만 Enemy 추가를 확인하는 아주 작은 단계에서는 ACTION_DOWN 때 Enemy 하나만 먼저 만들어 본다.
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            val pt = gctx.metrics.fromScreen(event.x, event.y)
            val enemy = Enemy(gctx, pt.x)
            world.add(enemy, Layer.ENEMY)
        }
        return player.onTouchEvent(event)
    }
}
