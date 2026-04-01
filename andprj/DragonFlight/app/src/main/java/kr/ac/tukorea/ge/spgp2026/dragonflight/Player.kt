package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Player(gctx: GameContext) : Sprite(gctx, R.mipmap.fighter) {
    override var width = 144f
    override var height = 160f
    override var x = 450f
    override var y = 1400f
}