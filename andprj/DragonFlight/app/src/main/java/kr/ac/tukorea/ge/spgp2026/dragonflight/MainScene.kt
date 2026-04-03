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
        CONTROLLER,
        UI,
    }

    // 이제는 JoyStick 같은 별도 입력 오브젝트를 두지 않고,
    // Player 가 직접 터치 방향을 해석해 좌/우 이동 방향을 결정한다.
    val player = Player(gctx)
    private val enemyGenerator = EnemyGenerator(gctx)
    private val collisionChecker = CollisionChecker(gctx)
    private val scoreLabel = ScoreLabel()

    // layer 가 enum 이면 Layer.entries 로 enum 전체를 그대로 꺼낼 수 있다.
    // 그래서 arrayOf(Layer.PLAYER, ...) 를 일일이 다시 적지 않아도 된다.
    // CONTROLLER layer 는 EnemyGenerator 같은 "화면에는 직접 안 보이지만,
    // 다른 오브젝트를 생성하거나 Scene 흐름을 관리하는 담당자"를 넣기 위한 자리이다.
    // 이 layer 를 ENEMY 뒤에 두면, 방금 생성된 Enemy 는 다음 프레임부터 움직이게 되어
    // 생성과 이동의 흐름을 구분해서 보기 쉽다.
    override val world = World(Layer.entries.toTypedArray()).apply {
        add(player, Layer.PLAYER)
        add(enemyGenerator, Layer.CONTROLLER)
        add(collisionChecker, Layer.CONTROLLER)
        add(scoreLabel, Layer.UI)
    }

    fun addScore(amount: Int) {
        scoreLabel.add(amount)
    }

    fun getScore(): Int {
        return scoreLabel.getScore()
    }

    // 현재 Scene 에서는 터치 입력을 따로 나누지 않고
    // 그대로 Player 에게 넘겨 Player 가 목표 위치를 직접 해석하게 한다.
    // Enemy 생성은 이제 EnemyGenerator 가 맡으므로,
    // MainScene 은 더 이상 touch down 때 Enemy 를 직접 만들지 않는다.
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return player.onTouchEvent(event)
    }
}
