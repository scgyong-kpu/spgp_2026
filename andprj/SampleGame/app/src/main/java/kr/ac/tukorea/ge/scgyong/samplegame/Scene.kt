package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Canvas
import android.view.MotionEvent

// Scene 은 한 화면 또는 한 게임 상태 단위를 뜻하는 공통 추상 타입이다.
// 나중에 MainScene, TitleScene, PauseScene 같은 여러 장면이 생겨도 GameView 는 Scene 타입만 알면 된다.
abstract class Scene(
    protected val gctx: GameContext,
) {
    open fun update(gctx: GameContext) {
    }

    open fun draw(canvas: Canvas) {
    }

    open fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}
