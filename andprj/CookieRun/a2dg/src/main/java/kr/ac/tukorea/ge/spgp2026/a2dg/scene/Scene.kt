package kr.ac.tukorea.ge.spgp2026.a2dg.scene

import android.graphics.Canvas
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.ITouchable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// Scene 은 한 화면 또는 한 게임 상태 단위를 뜻하는 공통 추상 타입이다.
// 나중에 MainScene, TitleScene, PauseScene 같은 여러 장면이 생겨도 GameView 는 Scene 타입만 알면 된다.
abstract class Scene(
    protected val gctx: GameContext,
) {
    // 보통의 Scene 은 World 를 하나 소유하고, 기본 update / draw 를 그 World 에 위임한다.
    // World 가 없는 특수 Scene 이라면 null 을 유지한 채 update / draw 를 직접 override 하면 된다.
    open val world: World<*>? = null
    open val clipsRect = false
    open val isTransparent = false

    // touch layer 에서 ACTION_DOWN 을 처리한 객체를 기억한다.
    // 예를 들어 Slide 버튼은 손가락이 버튼 밖으로 조금 움직여도 ACTION_UP 을 받아야
    // pressed=false 를 전달할 수 있으므로, DOWN 을 처리한 객체에게 이후 touch 를 먼저 보낸다.
    private var capturingTouchable: ITouchable? = null

    open fun update(gctx: GameContext) {
        world?.update(gctx)
    }

    open fun draw(canvas: Canvas) {
        world?.draw(canvas)
    }

    // Scene 이 stack 안에 들어오거나 나갈 때 호출되는 기본 생명주기 함수들이다.
    // 필요한 Scene 만 override 해서 초기화, 정리, 일시정지, 재개 처리를 넣으면 된다.
    open fun onEnter() {
    }

    open fun onExit() {
    }

    open fun onPause() {
    }

    open fun onResume() {
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

    // Scene 은 구체적인 layer enum 을 알지 못한다.
    // 그래서 MainScene 같은 하위 Scene 이 world.objectsAt(Layer.TOUCH) 를 반환하면,
    // 공통 Scene 은 그 목록에서 ITouchable 객체를 찾아 touch event 를 전달한다.
    protected open fun touchObjects(): List<IGameObject>? {
        return null
    }

    open fun onTouchEvent(event: MotionEvent): Boolean {
        val captured = capturingTouchable
        if (captured != null) {
            val processed = captured.onTouchEvent(event)
            if (!processed || event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
                capturingTouchable = null
            }
            return processed
        }

        // 아무 객체도 capture 중이 아닐 때는 ACTION_DOWN 만 새 touchable 을 찾는다.
        // MOVE/UP 은 원래 DOWN 을 처리한 객체에게 이어서 전달되어야 하므로,
        // capture 대상이 없다면 새 객체를 찾지 않고 처리하지 않은 것으로 둔다.
        if (event.actionMasked != MotionEvent.ACTION_DOWN) {
            return false
        }

        val objects = touchObjects() ?: return false
        for (i in objects.lastIndex downTo 0) {
            val touchable = objects[i] as? ITouchable ?: continue
            if (touchable.onTouchEvent(event)) {
                capturingTouchable = touchable
                return true
            }
        }
        return false
    }

    // 뒤로 가기 이벤트가 발생했을 때 호출된다.
    // 기본 동작은 현재 Scene 을 pop() 하는 것이다.
    // stack 에 Scene 이 하나뿐이면 pop() 하지 않고 false 를 반환한다.
    open fun onBackPressed(): Boolean {
        if (gctx.sceneStack.size <= 1) return false
        pop()
        return true
    }
}
