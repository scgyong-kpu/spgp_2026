package kr.ac.tukorea.ge.spgp2026.a2dg

// SceneStack 은 Scene 들을 stack 구조로 관리하는 가장 단순한 컨테이너이다.
// 지금 단계에서는 push / pop 과 현재 Scene 을 읽는 top 프로퍼티만 제공하고,
// 다음 단계에서 GameView 가 실제로 sceneStack.top 을 기준으로 처리하게 된다.
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
}
