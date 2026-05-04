package kr.ac.tukorea.ge.spgp2026.a2dg.scene

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// SceneStack 은 Scene 들을 stack 구조로 관리하는 가장 단순한 컨테이너이다.
// 지금은 push / pop / change 와 현재 Scene 을 읽는 top 프로퍼티를 제공한다.
class SceneStack(private val gctx: GameContext) {
    private val scenes = mutableListOf<Scene>()

    // Scene stack 이 앱 내부 UI/게임 로직에 의해 모두 비었을 때 바깥쪽으로 알려 주는 callback 이다.
    // a2dg 는 Activity 를 직접 모르므로, GameView 가 여기에 Activity.finish() 같은 처리를 등록한다.
    // 이렇게 하면 SceneStack 은 Android Activity 에 의존하지 않고, "stack 이 비었다"는 사건만 알리면 된다.
    var onEmptyStack: (() -> Unit)? = null

    val top: Scene?
        get() = scenes.lastOrNull()

    val size: Int
        get() = scenes.size

    val lastIndex: Int
        get() = scenes.lastIndex

    val isEmpty: Boolean
        get() = scenes.isEmpty()

    fun sceneAt(index: Int): Scene {
        return scenes[index]
    }

    fun push(scene: Scene) {
        top?.onPause()
        scenes.add(scene)
        gctx.scene = scene
        scene.onEnter()
    }

    fun pop(): Scene {
        val popped = scenes.removeAt(scenes.lastIndex)
        popped.onExit()
        top?.let {
            gctx.scene = it
            it.onResume()
        } ?: onEmptyStack?.invoke()
        return popped
    }

    private fun clearScenes() {
        while (scenes.isNotEmpty()) {
            scenes.removeAt(scenes.lastIndex).onExit()
        }
    }

    // popAll() 은 보통 게임 안 UI 나 게임 로직이
    // "현재 게임 stack 을 모두 끝내겠다"고 명시적으로 요청할 때 쓰는 함수이다.
    // 예를 들어 PauseScene 의 Exit 버튼은 gctx.sceneStack.popAll() 처럼 인자 없이 호출한다.
    // 그래서 기본값은 finishesActivity=true 로 두어, 게임 개발자가 인자 없이 호출하면 Activity 종료까지 이어진다.
    //
    // 반대로 Activity.onDestroy() 처럼 이미 Activity 가 종료되는 중인 lifecycle 정리에서는
    // popAll(finishesActivity=false) 를 호출해 Scene 의 onExit() 만 실행하고 finish callback 은 다시 부르지 않는다.
    // 즉 finishesActivity 는 "Scene 을 모두 제거한 뒤 Activity.finish() 요청까지 이어갈 것인가"를 뜻한다.
    //
    // Exit 버튼이 popAll(true) 를 호출하면 onEmptyStack 이 Activity.finish() 를 요청하고,
    // 그 결과 Android lifecycle 이 진행되면서 Activity.onDestroy() 에서 popAll(false) 가 다시 호출될 수 있다.
    // 이 두 번째 호출 시점에는 이미 scenes 가 비어 있으므로 clearScenes() 는 아무 것도 하지 않는 no-op 이고,
    // finishesActivity=false 이므로 finish callback 도 다시 호출하지 않는다.
    // 따라서 앱 내부 종료 요청과 lifecycle 정리 호출이 이어져도 안전하다.
    //
    // pop() 을 반복 호출하면 중간 Scene 이 잠깐 onResume() 되었다가 다시 pop 되는 부작용이 생길 수 있다.
    // 그래서 popAll() 은 위에서부터 onExit() 만 호출하며 모두 제거한다.
    fun popAll(finishesActivity: Boolean = true) {
        clearScenes()
        if (finishesActivity) {
            onEmptyStack?.invoke()
        }
    }

    // change 는 top 을 다른 Scene 으로 바꾸고, 나머지 stack 은 그대로 둔다.
    // push / pop 을 따로 호출하면 아래 Scene 이 잠깐 resume 되었다가 다시 pause 되므로,
    // change 는 그 중간 과정을 거치지 않고 맨 위 Scene 하나만 바로 교체한다.
    fun change(scene: Scene): Scene {
        if (scenes.isEmpty()) {
            push(scene)
            return scene
        }

        val previous = scenes.removeAt(scenes.lastIndex)
        previous.onExit()
        scenes.add(scene)
        gctx.scene = scene
        scene.onEnter()
        return previous
    }
}
