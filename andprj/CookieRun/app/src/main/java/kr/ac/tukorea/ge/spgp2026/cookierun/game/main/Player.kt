package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

class Player(gctx: GameContext): Sprite(gctx, R.mipmap.cookie_player) {
    init {
        setCenterProportionalWidth(200f, 700f, 200f)
    }
}
