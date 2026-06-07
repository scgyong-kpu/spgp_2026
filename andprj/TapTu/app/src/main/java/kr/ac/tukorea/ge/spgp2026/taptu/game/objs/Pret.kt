package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import android.text.BoringLayout.Metrics
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R


class Pret(gctx: GameContext, index: Int): Sprite(gctx, R.mipmap.trans_50p) {
    init {
        val w = gctx.metrics.width
        val h = gctx.metrics.height
        val x = NoteSprite.xFromPret(index)
        setCenter(x, h / 2)
        setSize(NoteSprite.X_SPACE, h)
    }
}