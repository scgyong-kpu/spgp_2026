package kr.ac.tukorea.ge.scgyong.samplegame

import android.view.MotionEvent

class MainScene(gctx: GameContext) : Scene(gctx) {
    // layer 종류는 World 가 아니라 MainScene 같은 게임 쪽에서 정의한다.
    // 그래야 서로 다른 게임이 각자 필요한 layer 구성을 자유롭게 가질 수 있다.
    // Fighter 같은 다른 객체가 나중에 Bullet 을 추가할 때도 이 layer 정의를 참조할 수 있어야 하므로
    // private 으로 숨기지 않고 MainScene 바깥에서도 접근 가능한 타입으로 둔다.
    // enum class 는 "정해진 몇 가지 종류만 가지는 타입"을 만들 때 쓰는 Kotlin 문법이다.
    // 여기서는 BALL, CIRCLE, FIGHTER 세 가지 layer 종류만 허용하겠다는 뜻이다.
    enum class Layer {
        BALL,
        CIRCLE,
        FIGHTER,
    }

    // Scene 은 화면 안에 어떤 GameObject 들이 존재하는지 구성하고, 그 객체들에 대한 update/draw/input 처리를 맡는다.
    // 반대로 GameView 는 Android View 생명주기와 프레임 루프를 담당하고, 실제 게임 내용은 Scene 에 넘긴다.
    // Fighter 는 onTouchEvent 에서 setTarget() 으로 직접 접근해야 하므로, gameObjects 안에만 숨기기보다
    // 멤버로 하나 들고 있는 편이 더 단순하고 읽기 쉽다.
    private val fighter = Fighter(gctx)
    // Scene 안의 모든 오브젝트를 gameObjects 에 모아 두면, 종류가 달라도 공통 반복문으로 처리할 수 있다.
    // MainScene 이 직접 gameObjects 목록을 들고 있기보다, World 를 하나 소유하고 그 안에 실제 GameObject 들을 담기 시작한다.
    // 이렇게 하면 Scene 은 게임 규칙과 입력 처리 쪽에 더 집중하고, 오브젝트 보관과 공통 순회는 World 쪽으로 넘길 수 있다.
    // World 는 공통 구조만 알고, 실제 layer 종류와 순서는 MainScene 이 정한다.
    // enum 전체 항목을 그대로 쓰면 Layer 에 항목이 추가되어도 여기 배열을 다시 고칠 필요가 없다.
    // Layer.entries 는 enum 안의 모든 항목을 선언된 순서대로 모아 둔 목록이다.
    // 즉 지금은 [BALL, CIRCLE, FIGHTER] 순서가 되고, 나중에 enum 에 항목을 추가하면 그 항목도 자동으로 포함된다.
    // World 생성자는 Array<TLayer> 를 받으므로, entries 목록을 toTypedArray() 로 Array 로 바꾸어 넘긴다.
    // 아래와 같이 명시적으로 나열할 수도 있다:
    // World(arrayOf(Layer.BALL, Layer.CIRCLE, Layer.FIGHTER))
    override val world = World(Layer.entries.toTypedArray()).apply {
        // apply 블록은 "객체를 하나 만든 직후, 그 객체를 바로 설정할 때" 자주 쓰는 Kotlin 문법이다.
        // 여기서는 막 만든 World 안에 어떤 오브젝트를 어느 layer 에 넣을지 이어서 적기 위해 사용한다.
        repeat(10) { add(Ball.random(gctx), Layer.BALL) }
        repeat(5) { add(BouncingCircle(gctx), Layer.CIRCLE) }
        add(fighter, Layer.FIGHTER)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
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
