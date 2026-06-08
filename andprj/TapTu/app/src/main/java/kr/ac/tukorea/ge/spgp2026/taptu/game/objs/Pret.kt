package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R


class Pret(gctx: GameContext, index: Int): Sprite(gctx, R.mipmap.trans_50p) {
    var shows = false
    init {
        val w = gctx.metrics.width
        val h = gctx.metrics.height
        val x = FallingNoteSprite.xFromPret(index)
        setCenter(x, h / 2)
        setSize(FallingNoteSprite.X_SPACE, h)
    }

    override fun draw(canvas: Canvas) {
        if (shows) {
            super.draw(canvas)
        }
    }
}