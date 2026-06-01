package kr.ac.tukorea.ge.spgp2026.taptu.game.scene.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.data.SongCatalog
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.taptu.res.BitmapBlur

class MainScene(
    gctx: GameContext,
    songIndex: Int,
) : Scene(gctx) {
    override val world = World<MainLayer>(MainLayer.entries.toTypedArray())
    val song = SongCatalog.songs[songIndex]

    override fun onEnter() {
        val screenWidth = gctx.metrics.width
        val screenHeight = gctx.metrics.height
        val centerX = screenWidth / 2
        val centerY = screenHeight / 2

        val context = gctx.view.context
        val thumbnail = song.loadThumbnail(context.assets) ?: gctx.res.getBitmap(R.mipmap.default_thumbnail)
        val blurredThumbnail = BitmapBlur.blurBitmap(context, thumbnail)
        val albumCover = Sprite(gctx, bitmap = blurredThumbnail, resId = R.mipmap.default_thumbnail)
        albumCover.setCenterProportionalHeight(
            centerX,
            centerY,
            screenHeight,
        )
        world.add(albumCover, MainLayer.BG)

        val bg = Sprite(gctx, R.mipmap.bg)
        bg.setCenterProportionalWidth(centerX, centerY, screenWidth)
        world.add(bg, MainLayer.BG)
    }
}
