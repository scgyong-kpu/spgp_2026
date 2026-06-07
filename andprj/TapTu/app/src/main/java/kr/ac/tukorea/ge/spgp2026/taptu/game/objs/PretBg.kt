package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.data.Song

class PretBg(gctx: GameContext,
             private val song: Song,
             private val musicTimeProvider: () -> Float,
): Sprite(gctx, kr.ac.tukorea.ge.spgp2026.taptu.R.mipmap.bg) {
    init {
        val screenWidth = gctx.metrics.width
        val screenHeight = gctx.metrics.height
        val centerX = screenWidth / 2
        val centerY = screenHeight / 2
        setCenterProportionalWidth(centerX, centerY, screenWidth)
    }
}
