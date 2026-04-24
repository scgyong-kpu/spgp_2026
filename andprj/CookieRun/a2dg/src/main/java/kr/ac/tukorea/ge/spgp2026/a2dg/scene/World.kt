package kr.ac.tukorea.ge.spgp2026.a2dg.scene

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameView

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
    // public inline 함수가 이 필드에 접근하므로,
    // Kotlin 에서는 private 대신 @PublishedApi internal 로 열어 두어야 한다.
    // 이렇게 하면 일반 사용 코드에는 여전히 숨기되, inline 으로 펴질 코드에서는 합법적으로 참조할 수 있다.
    private val orderedLayers = orderedLayers

    @PublishedApi
    internal val layers = orderedLayers.associateWith { mutableListOf<IGameObject>() }

    // recycleBin 은 "삭제되었지만 버리지 않고 다시 쓸 수 있는 객체들"을 타입별로 모아 두는 장소이다.
    // key 는 Bullet::class.java, Enemy::class.java 같은 실제 클래스이고,
    // value 는 그 타입의 재활용 대기 객체 목록이다.
    //
    // 여기서는 Class<out IRecyclable> 를 key 로 사용해
    // "아무 클래스"가 아니라 "IRecyclable 을 구현한 클래스"만 bin 에 들어온다는 의도를 드러낸다.
    //
    // ArrayList 를 쓰는 이유는:
    // - 재활용 풀은 보통 개수가 크지 않고
    // - 앞/뒤에서 하나 꺼내고 넣는 정도면 충분하며
    // - 별도 iterator 객체를 만들지 않고 바로 접근하기 쉽기 때문이다.
    private val recycleBin = HashMap<Class<out IRecyclable>, ArrayList<IRecyclable>>()

    // layer 별 목록 길이를 모두 더하면 현재 World 안에 들어 있는 전체 오브젝트 수가 된다.
    val objectCount: Int
        get() = layers.values.sumOf { it.size }

    fun countAt(layer: TLayer): Int {
        return layers.getValue(layer).size
    }

    // 어떤 layer 안에 있는 객체들을 바깥에서 읽어야 할 때가 있다.
    // 지금은 CollisionChecker 가 BULLET, ENEMY 목록을 순회하기 위해 이 함수를 사용한다.
    // 반환 타입은 MutableList 가 아니라 List 로 두어,
    // 바깥 코드가 이 목록 자체를 마음대로 add/remove 하지 않게 읽기 전용으로 제한한다.
    fun objectsAt(layer: TLayer): List<IGameObject> {
        return layers.getValue(layer)
    }

    // 같은 layer 안에서 조건에 맞는 객체를 지우는 경우에는
    // 뒤에서 앞으로 순회해야 index 가 당겨져도 아직 방문하지 않은 앞쪽 객체를 안전하게 계속 볼 수 있다.
    // 이 규칙을 World 안에 모아 두면 바깥 코드가 매번 lastIndex downTo 0 를 다시 적지 않아도 된다.
    // inline 으로 두면 작은 helper 를 자주 호출하는 경우 함수/람다 호출 부담을 줄이기 좋다.
    inline fun forEachReversedAt(layer: TLayer, action: (IGameObject) -> Unit) {
        val objects = layers.getValue(layer)
        for (i in objects.lastIndex downTo 0) {
            action(objects[i])
        }
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
        val removed = layers.getValue(layer).remove(gameObject)
        if (!removed) return false

        // 삭제된 객체가 IRecyclable 이면 그냥 버리지 않고 recycle bin 으로 모은다.
        // onRecycle() 은 "다음 init(...) 전에 정리해야 할 마지막 상태"를 비우는 hook 이다.
        if (gameObject is IRecyclable) {
            gameObject.onRecycle()
            collectRecyclable(gameObject)
        }
        return true
    }

    // 재활용 가능한 객체를 bin 에 넣는다.
    // getOrPut() 도 쓸 수 있지만, 지금 단계에서는 작은 lambda 생성 가능성까지 피하기 위해
    // null check 로 직접 분기한다.
    private fun collectRecyclable(gameObject: IRecyclable) {
        val clazz = gameObject.javaClass as Class<out IRecyclable>
        var bin = recycleBin[clazz]
        if (bin == null) {
            bin = ArrayList()
            recycleBin[clazz] = bin
        }
        bin.add(gameObject)
    }

    // obtain() 은 "재활용 bin 에 같은 타입 객체가 있으면 하나 꺼내고, 없으면 null"을 돌려준다.
    // 새로 만들지 여부까지 World 가 결정하면 reflection 이나 factory lambda 를 내부에서 고민해야 하므로,
    // 이번 단계에서는 recycle bin 조회까지만 맡고 새 생성은 각 타입의 factory 가 명시적으로 처리한다.
    //
    // 사용 예:
    // val bullet = world.obtain(Bullet::class.java) ?: Bullet(gctx)
    // bullet.init(gctx, x, y, power)
    //
    // 이렇게 두면 호출부는 lambda 없이도 재활용/신규생성을 모두 처리할 수 있고,
    // obtain() 자체는 중간 객체를 만들지 않는 단순 조회 함수로 남는다.
    fun <T : IRecyclable> obtain(clazz: Class<T>): T? {
        val bin = recycleBin[clazz]
        if (bin == null || bin.isEmpty()) {
            return null
        }
        @Suppress("UNCHECKED_CAST")
        return bin.removeAt(bin.lastIndex) as T
    }

    fun update(gctx: GameContext) {
        // 먼저 layer 들을 순서대로 돌고,
        // 각 layer 안에 들어 있는 GameObject 들은 뒤에서 앞으로 update 한다.
        // 이렇게 하면 어떤 객체가 update() 도중 자기 자신을 remove() 해도
        // 아직 방문하지 않은 앞쪽 index 들은 영향을 덜 받고 안전하게 계속 순회할 수 있다.
        //
        // 여기서는 layers.values 나 for (obj in layer) 형태를 쓰지 않고,
        // orderedLayers 배열과 index 기반 반복을 직접 사용한다.
        // 실제 프로파일링에서 ArrayList$Itr, LinkedHashMap$LinkedValueIterator 할당이 많이 보여
        // update/draw hot path 만큼은 iterator 없이 돌도록 정리한 것이다.
        var layerIndex = 0
        while (layerIndex < orderedLayers.size) {
            val layer = layers.getValue(orderedLayers[layerIndex])
            for (i in layer.lastIndex downTo 0) {
                layer[i].update(gctx)
            }
            layerIndex++
        }
    }

    fun draw(canvas: Canvas) {
        // draw 도 update 와 같은 순서로 layer 별 순회를 한다.
        // 따라서 어떤 layer 를 먼저 주었는지가 그리기 순서에도 그대로 반영된다.
        var layerIndex = 0
        while (layerIndex < orderedLayers.size) {
            val layer = layers.getValue(orderedLayers[layerIndex])
            var objectIndex = 0
            while (objectIndex < layer.size) {
                layer[objectIndex].draw(canvas)
                objectIndex++
            }
            layerIndex++
        }

        // 예전처럼 collision box 디버그 표시는 World 가 전체 객체를 한 번 더 훑으며 처리한다.
        // 이렇게 두면 CollisionChecker 가 Bullet/Enemy 전용 draw 책임을 따로 가질 필요가 없다.
        if (GameView.drawsDebugInfo) {
            layerIndex = 0
            while (layerIndex < orderedLayers.size) {
                val layer = layers.getValue(orderedLayers[layerIndex])
                var objectIndex = 0
                while (objectIndex < layer.size) {
                    val obj = layer[objectIndex]
                    if (obj is IBoxCollidable) {
                        canvas.drawRect(obj.collisionRect, bboxPaint)
                    }
                    objectIndex++
                }
                layerIndex++
            }
        }
    }

    companion object {
        private val bboxPaint by lazy {
            Paint().apply {
                style = Paint.Style.STROKE
                color = Color.RED
                strokeWidth = 3f
            }
        }
    }
}
