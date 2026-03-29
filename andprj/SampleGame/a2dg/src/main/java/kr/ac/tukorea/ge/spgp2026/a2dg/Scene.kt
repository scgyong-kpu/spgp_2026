package kr.ac.tukorea.ge.spgp2026.a2dg

import android.graphics.Canvas
import android.view.MotionEvent

// Scene 은 한 화면 또는 한 게임 상태 단위를 뜻하는 공통 추상 타입이다.
// 나중에 MainScene, TitleScene, PauseScene 같은 여러 장면이 생겨도 GameView 는 Scene 타입만 알면 된다.
abstract class Scene(
    protected val gctx: GameContext,
) {
    // 보통의 Scene 은 World 를 하나 소유하고, 기본 update / draw 를 그 World 에 위임한다.
    // World 가 없는 특수 Scene 이라면 null 을 유지한 채 update / draw 를 직접 override 하면 된다.
    open val world: World<*>? = null

    open fun update(gctx: GameContext) {
        world?.update(gctx)
    }

    open fun draw(canvas: Canvas) {
        world?.draw(canvas)
    }

    // Scene 안에서는 gctx.sceneStack 을 통해
    // push / pop / change 를 더 짧게 호출한다.
    fun push() {
        gctx.sceneStack.push(this)
    }

    fun pop(): Scene {
        return gctx.sceneStack.pop()
    }

    fun change(): Scene {
        return gctx.sceneStack.change(this)
    }

    open fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}
