package kr.ac.tukorea.ge.spgp2026.cookierun.game.obstacle

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.game.layers.MainLayer
import kr.ac.tukorea.ge.spgp2026.cookierun.game.map.MapObject

// Obstacle 은 CookieRun 장애물들의 공통 부모이다.
// stage 문자 'X' 는 SimpleObstacle, 'Y'/'Z' 는 AnimObstacle 처럼
// 실제 생성되는 하위 클래스는 MapObjectCatalog 가 결정한다.
//
// 여기에는 모든 장애물이 공유하는 레이어와 위치 계산만 둔다.
// 이렇게 해 두면 정적 장애물, 애니메이션 장애물, 낙하 장애물이
// 서로 다른 이미지/동작을 가지더라도 같은 배치 기준을 사용할 수 있다.
abstract class Obstacle(gctx: GameContext, resId: Int) : MapObject(gctx, resId) {
    // 장애물은 OBSTACLE 레이어에 올라간다.
    // MapLoader 는 생성된 MapObject 의 layer 를 보고 world.add() 하므로,
    // 각 MapObject 하위 클래스가 자기 레이어를 알려 주는 구조가 된다.
    override val layer = MainLayer.OBSTACLE

    // stage 파일의 문자 하나는 100x100 게임 좌표 한 칸을 뜻한다.
    // left/top 은 그 칸의 왼쪽 위 좌표이고,
    // 장애물은 그 칸의 가로 중앙과 아래쪽 바닥에 맞춰 세운다.
    //
    // width 는 하위 클래스가 결정한다.
    // SimpleObstacle 은 고정 폭을 쓰고,
    // AnimObstacle 은 type 에 따라 서로 다른 폭을 넘긴다.
    open fun init(left: Float, top: Float, width: Float) {
        val b_w = bitmap.width
        val b_h = bitmap.height
        // height 는 현재 bitmap 의 가로/세로 비율을 유지하도록 계산한다.
        // AnimObstacle 은 init() 전에 bitmap 을 해당 type 의 첫 프레임으로 바꾼 뒤
        // 이 공통 init() 을 호출해야 올바른 높이를 얻을 수 있다.
        this.width = width
        height = width / b_w * b_h
        // 장애물의 기준점은 tile 의 아래쪽 가운데이다.
        // left + 50f 는 100 너비 tile 의 중앙이고, top + 100f 는 tile 의 바닥이다.
        val right = left + 50f + width / 2
        val bottom = top + 100f
        this.dstRect.set(right - width, bottom - height, right, bottom)
    }

    // 실제 RectF backing field 와 inset ratio 는 줄인 판정이 필요한 하위 클래스가 직접 가진다.
    // 이 helper 는 "dstRect 에서 비율만큼 안쪽으로 줄인 collisionRect 를 계산한다"는
    // 공통 수식만 제공한다.
    protected fun updateCollisionRect(insets: FloatArray) {
        collisionRect.set(
            dstRect.left + width * insets[0],
            dstRect.top + height * insets[1],
            dstRect.right - width * insets[2],
            dstRect.bottom - height * insets[3],
        )
    }
}
