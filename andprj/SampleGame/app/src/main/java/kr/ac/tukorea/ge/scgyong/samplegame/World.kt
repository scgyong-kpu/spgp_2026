package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Canvas

// World 는 Scene 안에 존재하는 실제 GameObject 들을 담아 두는 컨테이너이다.
// 지금 단계에서는 단순히 오브젝트 목록을 보관하고 update / draw 를 공통 반복문으로 처리하는 역할만 맡는다.
// 나중에는 layer 지원이 들어가면서 Array of Array 같은 더 일반적인 구조로 커질 수 있다.
class World(
    initialObjects: Iterable<IGameObject> = emptyList(),
) {
    // 우선은 가장 단순한 MutableList 하나로 시작한다.
    // Scene 이 이 World 를 소유하고, World 가 다시 그 안의 GameObject 들을 소유하는 구조를 목표로 한다.
    private val gameObjects = initialObjects.toMutableList()

    fun add(gameObject: IGameObject) {
        gameObjects.add(gameObject)
    }

    fun update(gctx: GameContext) {
        for (obj in gameObjects) {
            obj.update(gctx)
        }
    }

    fun draw(canvas: Canvas) {
        for (obj in gameObjects) {
            obj.draw(canvas)
        }
    }
}
