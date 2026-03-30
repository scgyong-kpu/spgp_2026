package kr.ac.tukorea.ge.spgp2026.a2dg

class GameContext(
    val view: GameView,
    var frameTime: Float = 0f, // 이전 프레임과의 시간 간격을 초 단위로 저장하는 변수이다.
    var currentTimeNanos: Long = 0L, // doFrame() 에서 전달된 nanos 를 저장하는 변수이다.
) {
    // 크기, 좌표계 변환, 입력 역변환 같은 화면 관련 정보는 metrics 안에 모아 둔다.
    val metrics = GameMetrics()
    val sceneStack = SceneStack()
    val res = GameResources(view.resources)
}
