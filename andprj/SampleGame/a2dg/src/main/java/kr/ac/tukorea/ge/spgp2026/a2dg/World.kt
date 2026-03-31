package kr.ac.tukorea.ge.spgp2026.a2dg

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper

// World 는 Scene 안의 GameObject 들을 layer 별로 나누어 담는 컨테이너이다.
// 이 단계부터는 layer 를 단순 Int 인덱스로 고정하지 않고, 게임이 정의한 layer 타입을 외부에서 받아 사용한다.
// 이렇게 하면 World 는 재사용 가능한 공통 구조로 남고, 어떤 layer 종류를 쓸지는 각 게임(Scene) 쪽에서 정할 수 있다.
class World<TLayer>(
    // <TLayer> 는 "레이어 종류를 나타내는 타입"이 아직 정해지지 않았다는 뜻의 generic 문법이다.
    // MainScene 에서는 이 자리에 MainScene.Layer enum 이 들어오고, 다른 게임이라면 다른 enum 이 들어올 수 있다.
    orderedLayers: Array<TLayer>,
) {
    // 방법 1 실험:
    // 순회 중 add/remove 가 일어날 때 ConcurrentModificationException 을 피하려고,
    // 즉시 반영이 꼭 필요하지 않은 변경은 Handler 로 "조금 뒤에" 처리하도록 미룬다.
    //
    // 이 방법은 구현이 단순하지만 단점도 있다.
    // add/remove 요청을 한 직후에는 layers 안의 실제 목록이 아직 안 바뀌어 있을 수 있어서,
    // objectCount 나 getDebugCounts() 같은 디버그 값이 잠깐 실제 체감 상태와 어긋날 수 있다.
    // 예를 들어 Bullet 을 하나 발사했는데 이번 프레임의 count 에는 아직 안 보이거나,
    // 화면 밖으로 나간 Bullet 이 다음 프레임까지 count 에 잠깐 남아 있을 수 있다.
    private val mainHandler = Handler(Looper.getMainLooper())

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

    // 즉시 반영이 필요 없는 보통 경우에는 immediately=false 로 두고,
    // 초기 Scene 구성처럼 순회와 무관한 시점에만 immediately=true 를 사용한다.
    //
    // immediately=false:
    // - update/draw 순회와 겹칠 수 있는 런타임 add 요청
    // - Handler 로 메시지 큐에 넣어 두었다가 나중에 추가
    //
    // immediately=true:
    // - Scene 시작 시점처럼 아직 순회가 돌고 있지 않은 초기 구성
    // - layers 목록에 즉시 반영
    fun add(gameObject: IGameObject, layer: TLayer, immediately: Boolean = false) {
        val objects = layers.getValue(layer)
        if (immediately) {
            objects.add(gameObject)
            return
        }
        mainHandler.post {
            objects.add(gameObject)
        }
    }

    // remove 도 add 와 같은 규칙을 따른다.
    // 보통은 immediately=false 로 두어 나중에 지우고,
    // 순회와 관계없는 안전한 시점에만 immediately=true 를 쓸 수 있다.
    //
    // 이 방법의 단점은 add 와 마찬가지로,
    // 삭제 요청 직후에도 objectCount/getDebugCounts() 에는 잠깐 남아 보일 수 있다는 점이다.
    fun remove(gameObject: IGameObject, layer: TLayer, immediately: Boolean = false): Boolean {
        val objects = layers.getValue(layer)
        if (immediately) {
            return objects.remove(gameObject)
        }
        mainHandler.post {
            objects.remove(gameObject)
        }
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
