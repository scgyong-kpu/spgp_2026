package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.view.MotionEvent

// ITouchable 은 화면 터치 이벤트를 받을 수 있는 GameObject 의 공통 계약이다.
// Button, JoyStick 처럼 화면에 그려지는 객체가 직접 touch 처리도 맡을 때 구현한다.
//
// Scene 은 나중에 특정 World layer 에 들어 있는 객체 중 ITouchable 만 골라
// onTouchEvent() 를 호출하게 된다. 이렇게 하면 Button 은 World 에 의해 draw 되고,
// 동시에 Scene 의 touch dispatch 대상도 될 수 있다.
interface ITouchable {
    fun onTouchEvent(event: MotionEvent): Boolean
}
