package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

class Player(gctx: GameContext): Sprite(gctx, R.mipmap.cookie_player) {
    init {
        // 처음에는 움직임이나 애니메이션 없이, 화면에 보이는 플레이어 위치와 크기만 잡아 둔다.
        // 이후 Jump, Slide, 상태 애니메이션을 붙여도 Player 클래스 안에서 이어서 확장할 수 있다.
        setCenterProportionalWidth(200f, 700f, 200f)
    }
}
