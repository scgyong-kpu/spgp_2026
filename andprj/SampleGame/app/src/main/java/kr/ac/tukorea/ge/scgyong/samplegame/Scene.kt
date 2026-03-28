package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Canvas
import android.view.MotionEvent

// Scene 은 한 화면 또는 한 게임 상태 단위를 뜻하는 공통 추상 타입이다.
// 나중에 MainScene, TitleScene, PauseScene 같은 여러 장면이 생겨도 GameView 는 Scene 타입만 알면 된다.
abstract class Scene(
    protected val gctx: GameContext,
) {
    // 보통의 Scene 은 World 를 하나 소유하고, 기본 update / draw 를 그 World 에 위임한다.
    // World 가 없는 특수 Scene 이라면 null 을 유지한 채 update / draw 를 직접 override 하면 된다.
    // World 가 generic 이더라도 Scene 쪽은 구체 layer 타입까지 알 필요가 없으므로
    // 여기서는 World<*> 형태로 받아 공통 update / draw 만 위임한다.
    open val world: World<*>? = null

    open fun update(gctx: GameContext) {
        world?.update(gctx)
    }

    open fun draw(canvas: Canvas) {
        world?.draw(canvas)
    }

    open fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}
