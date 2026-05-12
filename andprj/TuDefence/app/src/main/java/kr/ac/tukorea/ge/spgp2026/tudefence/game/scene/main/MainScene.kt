package kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main

import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledMapLoader

class MainScene(gctx: GameContext): Scene(gctx) {
    init {
        val map = TiledMapLoader.load(gctx.view.context.assets, "map/desert.tmj")
        val layer = map.firstTileLayer()
        Log.d(
            javaClass.simpleName,
            "map=${map.width}x${map.height}, tile=${map.tilewidth}x${map.tileheight}, " +
                "layer='${layer.name}', data=${layer.data.size}, firstTile=${layer.tileAt(0, 0)}"
        )
    }
}
