package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Canvas
import android.view.MotionEvent

class MainScene(val gctx: GameContext) {
    // Scene 은 화면 안에 어떤 GameObject 들이 존재하는지 구성하고, 그 객체들에 대한 update/draw/input 처리를 맡는다.
    // 반대로 GameView 는 Android View 생명주기와 프레임 루프를 담당하고, 실제 게임 내용은 Scene 에 넘긴다.
    // Fighter 는 onTouchEvent 에서 setTarget() 으로 직접 접근해야 하므로, gameObjects 안에만 숨기기보다
    // 멤버로 하나 들고 있는 편이 더 단순하고 읽기 쉽다.
    private val fighter = Fighter(gctx)
    // Scene 안의 모든 오브젝트를 gameObjects 에 모아 두면, 종류가 달라도 공통 반복문으로 처리할 수 있다.
    private val gameObjects = buildList<IGameObject> {
        repeat(10) { add(Ball.random(gctx)) }
        repeat(5) { add(BouncingCircle(gctx)) }
        add(fighter)
    }.toTypedArray()

    fun update(gctx: GameContext) {
        // 각 GameObject 는 Scene 이 준비한 공통 문맥 gctx 를 받아 자기 상태를 갱신한다.
        for (obj in gameObjects) {
            obj.update(gctx)
        }
    }

    fun draw(canvas: Canvas) {
        // Scene 안에 들어 있는 모든 GameObject 를 같은 순회 구조로 그린다.
        for (obj in gameObjects) {
            obj.draw(canvas)
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // event.x, event.y 는 실제 화면 좌표이므로, 역행렬을 적용해 가상 좌표계 값으로 되돌린다.
                val pt = gctx.metrics.fromScreen(event.x, event.y)
                // 입력은 Fighter 전용 동작이므로, Scene 이 Fighter 에 직접 전달해 목표 위치를 바꾼다.
                fighter.setTarget(pt.x, pt.y)
                return true
            }
        }
        return false
    }
}
