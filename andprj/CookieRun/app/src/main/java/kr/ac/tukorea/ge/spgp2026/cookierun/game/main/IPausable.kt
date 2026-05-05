package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

// IPausable 은 Scene 이 pause/resume 될 때 함께 멈추거나 다시 시작해야 하는 객체가 구현한다.
//
// World.update() 로만 움직이는 객체는 Scene 이 pause 되면 update() 자체가 호출되지 않으므로
// 별도 처리가 필요 없다. 하지만 FallingObstacle 의 ValueAnimator 처럼
// Android framework 가 Scene update 와 별도로 실행하는 작업은 직접 pause/resume 해야 한다.
//
// 이 인터페이스를 쓰면 MainScene 은 FallingObstacle 같은 구체 클래스를 몰라도,
// "pause/resume 이 필요한 객체"라는 역할만 보고 처리할 수 있다.
interface IPausable {
    fun pause()
    fun resume()
}
