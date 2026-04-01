package kr.ac.tukorea.ge.spgp2026.a2dg

import android.graphics.Canvas

// World 는 Scene 안의 GameObject 들을 layer 별로 나누어 담는 컨테이너이다.
// 이 단계부터는 layer 를 단순 Int 인덱스로 고정하지 않고, 게임이 정의한 layer 타입을 외부에서 받아 사용한다.
// 이렇게 하면 World 는 재사용 가능한 공통 구조로 남고, 어떤 layer 종류를 쓸지는 각 게임(Scene) 쪽에서 정할 수 있다.
class World<TLayer>(
    // <TLayer> 는 "레이어 종류를 나타내는 타입"이 아직 정해지지 않았다는 뜻의 generic 문법이다.
    // MainScene 에서는 이 자리에 MainScene.Layer enum 이 들어오고, 다른 게임이라면 다른 enum 이 들어올 수 있다.
    orderedLayers: Array<TLayer>,
) {
    // 전달받은 layer 순서를 draw / update 순서의 기준으로 사용한다.
    // associateWith 는 "각 layer 값을 key 로 하고, 거기에 대응하는 빈 목록을 value 로 만든다"는 뜻이다.
    // 결과적으로 layers 는
    //   Layer.BALL -> MutableList<IGameObject>
    //   Layer.CIRCLE -> MutableList<IGameObject>
    //   Layer.FIGHTER -> MutableList<IGameObject>
    // 같은 map 구조가 된다.
    private val layers = orderedLayers.associateWith { mutableListOf<IGameObject>() }

    fun add(gameObject: IGameObject, layer: TLayer) {
        // getValue(layer) 는 해당 layer 에 대응하는 목록을 꺼낸다는 뜻이다.
        // 그리고 그 목록에 gameObject 를 추가한다.
        layers.getValue(layer).add(gameObject)
    }

    fun update(gctx: GameContext) {
        // 먼저 layer 들을 순서대로 돌고,
        // 각 layer 안에 들어 있는 GameObject 들을 다시 순서대로 update 한다.
        for (layer in layers.values) {
            for (obj in layer) {
                obj.update(gctx)
            }
        }
    }

    fun draw(canvas: Canvas) {
        // draw 도 update 와 같은 순서로 layer 별 순회를 한다.
        // 따라서 어떤 layer 를 먼저 주었는지가 그리기 순서에도 그대로 반영된다.
        for (layer in layers.values) {
            for (obj in layer) {
                obj.draw(canvas)
            }
        }
    }
}
