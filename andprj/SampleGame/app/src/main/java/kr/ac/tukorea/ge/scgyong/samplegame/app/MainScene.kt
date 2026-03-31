package kr.ac.tukorea.ge.scgyong.samplegame.app

import android.util.Log
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.World

class MainScene(gctx: GameContext) : Scene(gctx) {
    // layer 종류는 World 가 아니라 MainScene 같은 게임 쪽에서 정의한다.
    // 그래야 게임마다 필요한 layer 구성을 자유롭게 바꿀 수 있다.
    enum class Layer {
        BALL,
        CIRCLE,
        FIGHTER,
        JOYSTICK,
    }

    private val joyStick = JoyStick(gctx)
    // Fighter 는 이제 JoyStick 의 angle, power 를 직접 읽어 움직인다.
    // 그래서 생성할 때 JoyStick 참조를 함께 넘긴다.
    private val fighter = Fighter(gctx, joyStick)

    // MainScene 은 실제 GameObject 구성과 배치를 담당하고,
    // World 는 그 GameObject 들을 layer 순서대로 update / draw 하는 역할을 맡는다.
    override val world = World(Layer.entries.toTypedArray()).apply {
        repeat(10) { add(Ball.random(gctx), Layer.BALL) }
        repeat(5) { add(BouncingCircle(gctx), Layer.CIRCLE) }
        add(fighter, Layer.FIGHTER)
        add(joyStick, Layer.JOYSTICK)
    }

    override fun onEnter() {
        Log.d(javaClass.simpleName, "onEnter")
    }

    override fun onExit() {
        Log.d(javaClass.simpleName, "onExit")
    }

    override fun onPause() {
        Log.d(javaClass.simpleName, "onPause")
    }

    override fun onResume() {
        Log.d(javaClass.simpleName, "onResume")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 왼쪽 위의 작은 영역은 SceneStack 테스트 진입점으로 사용한다.
        // 여기서 Scene 1 을 push 해 두면,
        // Scene 1 -> Scene 2 -> pop -> Scene 1 -> pop 흐름을 바로 확인할 수 있다.
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            val pt = gctx.metrics.fromScreen(event.x, event.y)
            if (pt.x < 180f && pt.y < 180f) {
                StackTestScene1(gctx).push()
                return true
            }
        }

        return joyStick.onTouchEvent(event)
    }
}
