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
        ENEMY,
    }

    // 이제는 JoyStick 같은 별도 입력 오브젝트를 두지 않고,
    // Player 가 직접 터치 방향을 해석해 좌/우 이동 방향을 결정한다.
    val player = Player(gctx)

    // World(arrayOf(...)) 에 넘기는 layer 순서가 곧 update / draw 순서가 된다.
    // ENEMY layer 는 이번 단계에서 touch down 으로 Enemy 를 하나 만들어 보며 확인할 자리이다.
    // 아직 움직임이나 생성 규칙은 없지만, Player / Bullet 과 분리해 두면 다음 단계로 이어가기 쉽다.
    override val world = World(arrayOf(Layer.PLAYER, Layer.BULLET, Layer.ENEMY)).apply {
        add(player, Layer.PLAYER)
    }

    // 현재 Scene 에서는 터치 입력을 따로 나누지 않고
    // 그대로 Player 에게 넘겨 Player 가 목표 위치를 직접 해석하게 한다.
    // 다만 Enemy 추가를 확인하는 아주 작은 단계에서는 ACTION_DOWN 때 Enemy 하나만 먼저 만들어 본다.
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            val pt = gctx.metrics.fromScreen(event.x, event.y)
            val enemyY = Enemy.ENEMY_HEIGHT / 2f
            val enemy = Enemy(gctx, pt.x, enemyY)
            world.add(enemy, Layer.ENEMY)
        }
        return player.onTouchEvent(event)
    }
}
