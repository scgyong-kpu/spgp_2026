package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// SimpleObstacle 은 stage 파일의 'X' 문자로 생성되는 정적 장애물이다.
// 한 장의 bitmap 만 사용하고, 위치가 정해진 뒤에는 MapObject 의 공통 스크롤만 따른다.
class SimpleObstacle(gctx: GameContext) : Obstacle(gctx, R.mipmap.epn01_tm01_jp1a) {
    companion object {
        // MapObjectCatalog 에 등록된 'X' 생성 규칙은 이 get() 을 통해 장애물을 얻는다.
        // World 에 재활용 가능한 객체가 있으면 새로 만들지 않고 다시 초기화해서 사용한다.
        fun get(gctx: GameContext, left: Float, top: Float): Obstacle {
            val world = (gctx.scene as MainScene).world
            // World 에서 재활용 가능한 SimpleObstacle 이 있는지 찾아본다.
            // Obstacle 이 아니라 구체 클래스인 SimpleObstacle 로 찾아야
            // 다른 장애물 타입과 recycle bin 이 섞이지 않는다.
            val obs = world.obtain(SimpleObstacle::class.java) ?: SimpleObstacle(gctx)
            obs.init(left, top, WIDTH)
            return obs
        }
        // stage tile 하나가 100f 폭이므로, 장애물은 그보다 조금 좁은 80f 폭으로 표시한다.
        private const val WIDTH = 80f
    }
}
