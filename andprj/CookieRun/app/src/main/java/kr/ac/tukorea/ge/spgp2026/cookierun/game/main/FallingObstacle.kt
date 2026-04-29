package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// FallingObstacle 은 stage 파일의 'W' 문자로 생성되는 낙하형 장애물이다.
// 지금 단계에서는 우선 Obstacle 의 공통 배치 로직으로 화면에 보이게만 하고,
// 실제로 위에서 떨어지는 움직임은 다음 단계에서 update() 로 추가할 수 있다.
//
// SimpleObstacle 과 마찬가지로 이미지 한 장을 쓰지만,
// 별도 클래스로 분리해 두면 나중에 낙하 애니메이션, 충돌 박스 보정,
// pause/resume 처리 등을 다른 장애물과 섞지 않고 추가할 수 있다.
class FallingObstacle(gctx: GameContext): Obstacle(gctx, R.mipmap.epn01_tm01_sda) {
    companion object {
        // MapObjectCatalog 에 등록된 'W' 생성 규칙은 이 get() 을 통해 장애물을 얻는다.
        // World 에 재활용 가능한 객체가 있으면 새로 만들지 않고 다시 초기화해서 사용한다.
        fun get(gctx: GameContext, left: Float, top: Float): Obstacle {
            val world = (gctx.scene as MainScene).world
            // World 에서 재활용 가능한 FallingObstacle 이 있는지 찾아본다.
            // 낙하 장애물은 이후 상태값이 추가될 수 있으므로,
            // SimpleObstacle 과 recycle bin 이 섞이지 않도록 구체 클래스로 obtain 한다.
            val obs = world.obtain(FallingObstacle::class.java) ?: FallingObstacle(gctx)
            obs.init(left, top, WIDTH)
            return obs
        }
        // stage tile 하나가 100f 폭이므로, 원본 이미지 비율을 유지하면서
        // tile 보다 살짝 넓게 보이도록 108f 를 기준 폭으로 잡는다.
        private const val WIDTH = 108f
    }
}
