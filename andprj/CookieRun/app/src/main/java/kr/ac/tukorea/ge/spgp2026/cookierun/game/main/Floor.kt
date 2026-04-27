package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// Floor 는 플레이어가 밟는 바닥 타일을 뜻한다.
// 배경처럼 연속 스크롤하는 대상이 아니라, type 에 따라 서로 다른 크기/이미지의 타일을 배치하는 용도다.
class Floor(
    gctx: GameContext,
    private val type: Type,
) : Sprite(gctx, type.resId) {

    init {
        // type 에 들어 있는 width/height 는 이 타일을 게임 화면 안에서 얼마나 크게 그릴지 나타내는 값이다.
        // bitmap 원본 크기와는 별개이므로, type 하나만 바꾸면 같은 이미지 계열도 다른 크기의 바닥으로 쓸 수 있다.
        setSize(type.width, type.height)
    }

    override fun update(gctx: GameContext) {
        // Floor 는 왼쪽으로 계속 흐르는 바닥 타일이다.
        // speed 는 클래스 안에서 고정 상수로 두고, 모든 바닥 타일이 같은 속도로 움직이게 한다.
        x += SPEED * gctx.frameTime
        syncDstRect()
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

    companion object {
        // 바닥 타일은 화면 왼쪽으로 일정하게 흐른다.
        // 음수 값이므로 x 가 줄어들고, 결과적으로 왼쪽으로 이동한다.
        const val SPEED = -300f
    }
}
