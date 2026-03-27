package kr.ac.tukorea.ge.scgyong.samplegame

import android.graphics.Canvas

// World 는 Scene 안에 존재하는 실제 GameObject 들을 담아 두는 컨테이너이다.
// 이번 단계부터는 오브젝트를 단일 목록 하나에만 두지 않고, layer 별 목록으로 나누어 관리한다.
// 이렇게 해 두면 나중에 배경, 플레이어, 적, UI 같은 순서를 layer 로 분리해 다룰 수 있다.
class World(
    layerCount: Int = 1,
) {
    // 지금은 가장 단순하게 "레이어 개수"만 받아 Array<MutableList<IGameObject>> 형태로 시작한다.
    // 각 layer 는 같은 깊이에 있는 GameObject 들의 목록이라고 생각하면 된다.
    private val layers = Array(layerCount) { mutableListOf<IGameObject>() }

    fun add(gameObject: IGameObject, layerIndex: Int = 0) {
        layers[layerIndex].add(gameObject)
    }

    fun update(gctx: GameContext) {
        for (layer in layers) {
            for (obj in layer) {
                obj.update(gctx)
            }
        }
    }

    fun draw(canvas: Canvas) {
        for (layer in layers) {
            for (obj in layer) {
                obj.draw(canvas)
            }
        }
    }
}
