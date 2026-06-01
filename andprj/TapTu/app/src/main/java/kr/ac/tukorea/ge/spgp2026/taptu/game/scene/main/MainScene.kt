package kr.ac.tukorea.ge.spgp2026.taptu.game.scene.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.data.SongCatalog
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.MainLayer

class MainScene(
    gctx: GameContext,
    songIndex: Int,
) : Scene(gctx) {
    override val world = World<MainLayer>(MainLayer.entries.toTypedArray())
    val song = SongCatalog.songs[songIndex]

    override fun onEnter() {
        val bg = Sprite(gctx, R.mipmap.bg)
        bg.setCenterProportionalWidth(gctx.metrics.width / 2, gctx.metrics.height / 2, gctx.metrics.width)
        world.add(bg, MainLayer.BG)
    }
}
