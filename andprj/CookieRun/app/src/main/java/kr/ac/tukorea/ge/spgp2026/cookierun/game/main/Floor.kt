package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// Floor 는 MapObject 아래에서 동작하는 플레이어가 밟는 바닥 타일을 뜻한다.
// 배경처럼 연속 스크롤하는 대상이 아니라, type 에 따라 서로 다른 크기/이미지의 타일을 배치하는 용도다.
class Floor private constructor(
    val gctx: GameContext,
    private var type: Type,
) : MapObject(gctx, type.resId) {
    // Floor 는 항상 바닥 레이어에만 놓이는 오브젝트다.
    // layer 를 멤버 변수로 저장하지 않고 getter 로만 돌려주면, 객체마다 추가 메모리를 쓰지 않는다.
    override val layer get() = MainScene.Layer.FLOOR

    init {
        dstRect.set(0f, 0f, type.width, type.height)
        this.width = type.width
        this.height = type.height

        Log.d(javaClass.simpleName, "Created: $this")
    }

    // 생성 후 위치를 초기화하도록 한다. 이 함수는 재활용 된 뒤에도 불릴 예정이다
    fun init(type: Type, left: Float, top: Float) {
        this.bitmap = gctx.res.getBitmap(type.resId)
        this.type = type
        dstRect.set(left, top, left + type.width, top + type.height)

        this.width = type.width
        this.height = type.height
    }

    // Floor.Type 은 바닥 타일의 종류를 구분하는 enum 이다.
    // enum 은 "미리 정해 둔 이름 목록"이라고 생각하면 된다.
    // 그리고 각 이름에는 resId, width, height 같은 값을 묶어서 함께 붙일 수 있다.
    // 이렇게 이름과 값이 연결되는 방식을 value association 이라고 볼 수 있다.
    // 즉 T_10x2 라는 이름을 고르면, 그 이름에 연결된 이미지와 크기 정보가 같이 따라온다.
    enum class Type(
        val resId: Int,
        val width: Float,
        val height: Float,
    ) {
        // 길고 낮은 기본 바닥이다.
        // 이름 뒤 괄호 안의 값들이 enum 인스턴스에 들어가는 실제 데이터다.
        T_10x2(
            R.mipmap.cookierun_floor_480x48,
            1000f, 200f,
        ),
        // 거의 정사각형에 가까운 블록형 바닥이다.
        T_2x2(
            R.mipmap.cookierun_floor_124x120,
            200f, 200f,
        ),
        // 가로로 길고 세로가 얕은 작은 바닥이다.
        T_3x1(
            R.mipmap.cookierun_floor_120x40,
            300f, 100f,
        ),
    }

    override fun toString(): String {
        return "Floor(${this.type},@${System.identityHashCode(this)})"
    }

    companion object {
        fun get(gctx: GameContext, type: Type, left: Float, top: Float): Floor {
            val world = (gctx.scene as MainScene).world
            // World 에서 재활용 가능한 Floor 가 있는지 찾아본다.
            val floor = world.obtain(Floor::class.java) ?: Floor(gctx, type)
            floor.init(type, left, top)
            return floor
        }
    }
}
