package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// Obstacle 은 stage 파일의 'X' 문자에서 생성되는 가장 기본 장애물이다.
// Floor, JellyItem 과 마찬가지로 MapObject 를 상속하므로
// 공통 스크롤 속도, 화면 밖 제거, 재활용 구조를 그대로 사용한다.
class Obstacle(gctx: GameContext) : MapObject(gctx, R.mipmap.epn01_tm01_jp1a, 0f, 0f, WIDTH, WIDTH) {

    // 장애물은 OBSTACLE 레이어에 올라간다.
    // MapLoader 는 생성된 MapObject 의 layer 를 보고 world.add() 하므로,
    // 각 MapObject 하위 클래스가 자기 레이어를 알려 주는 구조가 된다.
    override val layer = MainScene.Layer.OBSTACLE

    // stage 파일의 문자 하나는 100x100 게임 좌표 한 칸을 뜻한다.
    // left/top 은 그 칸의 왼쪽 위 좌표이고,
    // 장애물은 그 칸의 가로 중앙과 아래쪽 바닥에 맞춰 세운다.
    fun init(left: Float, top: Float) {
        val b_w = bitmap.width
        val b_h = bitmap.height
        // WIDTH 는 게임 좌표계에서 장애물을 어느 정도 폭으로 보이게 할지 정한 값이다.
        // height 는 원본 bitmap 의 가로/세로 비율을 유지하도록 계산한다.
        width = WIDTH
        height = WIDTH / b_w * b_h
        // 장애물의 기준점은 tile 의 아래쪽 가운데이다.
        // left + 50f 는 100 너비 tile 의 중앙이고, top + 100f 는 tile 의 바닥이다.
        val right = left + 50f + width / 2
        val bottom = top + 100f
        this.dstRect.set(right - width, bottom - height, right, bottom)
    }

    companion object {
        // MapObjectCatalog 에 등록된 생성 규칙은 이 get() 을 통해 Obstacle 을 얻는다.
        // World 에 재활용 가능한 객체가 있으면 새로 만들지 않고 다시 초기화해서 사용한다.
        fun get(gctx: GameContext, left: Float, top: Float): Obstacle {
            val world = (gctx.scene as MainScene).world
            // World 에서 재활용 가능한 Obstacle 이 있는지 찾아본다.
            val obs = world.obtain(Obstacle::class.java) ?: Obstacle(gctx)
            obs.init(left, top)
            return obs
        }
        // stage tile 하나가 100f 폭이므로, 장애물은 그보다 조금 좁은 80f 폭으로 표시한다.
        private const val WIDTH = 80f
    }
}
