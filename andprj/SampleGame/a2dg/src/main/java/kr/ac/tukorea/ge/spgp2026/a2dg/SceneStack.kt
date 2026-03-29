package kr.ac.tukorea.ge.spgp2026.a2dg

// SceneStack 은 Scene 들을 stack 구조로 관리하는 가장 단순한 컨테이너이다.
// 지금은 push / pop / change 와 현재 Scene 을 읽는 top 프로퍼티를 제공한다.
class SceneStack {
    private val scenes = mutableListOf<Scene>()

    val top: Scene
        get() = scenes.last()

    fun push(scene: Scene) {
        scenes.add(scene)
    }

    fun pop(): Scene {
        return scenes.removeAt(scenes.lastIndex)
    }

    // change 는 top 을 다른 Scene 으로 바꾸고 싶을 때 쓴다.
    // stack 전체를 비우지 않고 맨 위 Scene 하나만 교체하는 동작이다.
    fun change(scene: Scene): Scene {
        val previous = pop()
        push(scene)
        return previous
    }
}
