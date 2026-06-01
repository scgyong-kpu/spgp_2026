package kr.ac.tukorea.ge.spgp2026.a2dg.view

import kr.ac.tukorea.ge.spgp2026.a2dg.res.GameResources
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.SceneStack

class GameContext(
    val view: GameView,
    var frameTime: Float = 0f, // 이전 프레임과의 시간 간격을 초 단위로 저장하는 변수이다.
    var currentTimeNanos: Long = 0L, // doFrame() 에서 전달된 nanos 를 저장하는 변수이다.
) {
    // 크기, 좌표계 변환, 입력 역변환 같은 화면 관련 정보는 metrics 안에 모아 둔다.
    val metrics = GameMetrics()
    val sceneStack = SceneStack(this)
    val res = GameResources(view.resources, view.context)

    // 여러 게임 오브젝트가 "현재 Scene" 에 접근해야 하는 경우가 실제로 자주 있다.
    // 예를 들면 Bullet 같은 새 오브젝트를 현재 Scene/World 에 추가하거나,
    // 현재 Scene 의 상태를 참고해야 하는 순간들이 있다.
    //
    // 그래서 GameContext 는 현재 활성 Scene 을 함께 들고 있도록 한다.
    // 이 값의 갱신 책임은 SceneStack 이 맡는다.
    lateinit var scene: Scene
}
