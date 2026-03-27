package kr.ac.tukorea.ge.scgyong.samplegame

data class GameContext(
    val view: GameView,
    val worldWidth: Float,
    val worldHeight: Float,
    var frameTime: Float = 0f, // 이전 프레임과의 시간 간격을 초 단위로 저장하는 변수이다.
    var currentTimeNanos: Long = 0L, // doFrame() 에게 전달된 nanos 를 저장하는 변수이다.
)
