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

    // 방법 2:
    // 순회 도중 add/remove 요청이 오면 즉시 목록을 건드리지 않고,
    // "어떤 오브젝트를 어느 layer 에 넣거나 뺄지"를 큐에 기록해 둔다.
    // 그 다음 update() 의 layer 순회가 모두 끝난 뒤 한 번에 반영한다.
    //
    // 이런 방식은 게임이 아니더라도 일반적인 컬렉션 순회 코드에서
    // ConcurrentModificationException 을 피할 때 자주 사용하는 대표적인 방법 중 하나이다.
    // 즉 "순회 중에는 수정 요청만 모아 두고, 순회가 끝난 뒤 일괄 반영한다"는 패턴으로 이해하면 된다.
    //
    // 여기서는 gameObject 만 저장하면 어느 목록에 넣고 빼야 할지 모르므로,
    // layer 도 함께 기억하는 PendingEntry 구조를 사용한다.
    private data class PendingEntry<TLayer>(
        val gameObject: IGameObject,
        val layer: TLayer,
    )

    private val objectsToAdd = mutableListOf<PendingEntry<TLayer>>()
    private val objectsToRemove = mutableListOf<PendingEntry<TLayer>>()

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

    // 즉시 반영이 필요 없는 보통 경우에는 immediately=false 로 두고,
    // 초기 Scene 구성처럼 순회와 무관한 시점에만 immediately=true 를 사용한다.
    //
    // immediately=false:
    // - update/draw 순회와 겹칠 수 있는 런타임 add 요청
    // - objectsToAdd 큐에만 기록해 두고, update 끝에서 한 번에 추가
    //
    // immediately=true:
    // - Scene 시작 시점처럼 아직 순회가 돌고 있지 않은 초기 구성
    // - layers 목록에 즉시 반영
    fun add(gameObject: IGameObject, layer: TLayer, immediately: Boolean = false) {
        if (immediately) {
            layers.getValue(layer).add(gameObject)
            return
        }
        objectsToAdd.add(PendingEntry(gameObject, layer))
    }

    // remove 도 add 와 같은 규칙을 따른다.
    // 보통은 immediately=false 로 두어 나중에 지우고,
    // 순회와 관계없는 안전한 시점에만 immediately=true 를 쓸 수 있다.
    //
    // 이 방법은 Handler 방식보다 한 프레임 안에서 더 예측 가능하지만,
    // 삭제 요청 직후에는 아직 layers 에 남아 있으므로 이번 update 중에는 계속 보일 수 있다.
    fun remove(gameObject: IGameObject, layer: TLayer, immediately: Boolean = false): Boolean {
        if (immediately) {
            return layers.getValue(layer).remove(gameObject)
        }
        objectsToRemove.add(PendingEntry(gameObject, layer))
        return true
    }

    fun update(gctx: GameContext) {
        // 먼저 layer 들을 순서대로 돌고,
        // 각 layer 안에 들어 있는 GameObject 들을 다시 순서대로 update 한다.
        for (layer in layers.values) {
            for (obj in layer) {
                obj.update(gctx)
            }
        }
        flushPendingChanges()
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

    private fun flushPendingChanges() {
        // 같은 프레임에 remove 와 add 가 함께 들어온 경우,
        // 먼저 remove 를 반영한 뒤 add 를 반영하면 결과를 더 직관적으로 이해하기 쉽다.
        for (entry in objectsToRemove) {
            layers.getValue(entry.layer).remove(entry.gameObject)
        }
        objectsToRemove.clear()

        for (entry in objectsToAdd) {
            layers.getValue(entry.layer).add(entry.gameObject)
        }
        objectsToAdd.clear()
    }
}
