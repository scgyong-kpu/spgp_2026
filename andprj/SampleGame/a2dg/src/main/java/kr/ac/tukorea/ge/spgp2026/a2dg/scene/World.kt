package kr.ac.tukorea.ge.spgp2026.a2dg.scene

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

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

    // layer 별 목록 길이를 모두 더하면 현재 World 안에 들어 있는 전체 오브젝트 수가 된다.
    val objectCount: Int
        get() = layers.values.sumOf { it.size }

    fun countAt(layer: TLayer): Int {
        return layers.getValue(layer).size
    }

    fun getDebugCounts(): String {
        return buildString {
            append('[')
            var first = true
            for (gameObjects in layers.values) {
                if (!first) append(", ")
                append(gameObjects.size)
                first = false
            }
            append(']')
        }
    }

    // 방법 3 최종 선택:
    // 게임에서는 실제로 "오브젝트가 자기 자신을 삭제하는 경우"가 더 자주 문제 된다.
    // 예를 들면 Bullet 이 화면 밖으로 나가면 자기 자신을 remove() 하는 식이다.
    //
    // 반면 add 는 보통 다른 레이어에서 일어나거나,
    // 적어도 지금 프로젝트에서는 Fighter 가 BULLET layer 에 추가하는 식으로 동작한다.
    // 그래서 add/remove 둘 다 pending 으로 일반화하지 않고,
    // update loop 를 거꾸로 도는 방식으로 "자기 자신 삭제" 문제를 해결한다.
    //
    // 이 방식에서는 add/remove 를 즉시 반영해도,
    // 현재 순회 중인 index 뒤쪽 항목을 remove 하게 되므로 안전하게 계속 진행할 수 있다.
    fun add(gameObject: IGameObject, layer: TLayer) {
        layers.getValue(layer).add(gameObject)
    }

    fun remove(gameObject: IGameObject, layer: TLayer): Boolean {
        return layers.getValue(layer).remove(gameObject)
    }

    fun update(gctx: GameContext) {
        // 먼저 layer 들을 순서대로 돌고,
        // 각 layer 안에 들어 있는 GameObject 들은 뒤에서 앞으로 update 한다.
        // 이렇게 하면 어떤 객체가 update() 도중 자기 자신을 remove() 해도
        // 아직 방문하지 않은 앞쪽 index 들은 영향을 덜 받고 안전하게 계속 순회할 수 있다.
        for (layer in layers.values) {
            for (i in layer.lastIndex downTo 0) {
                layer[i].update(gctx)
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
