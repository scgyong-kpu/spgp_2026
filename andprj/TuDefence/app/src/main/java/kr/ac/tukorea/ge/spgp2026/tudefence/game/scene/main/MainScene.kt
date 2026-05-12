package kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main

import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.bg.TiledBackground

class MainScene(gctx: GameContext): Scene(gctx) {
    enum class Layer {
        BG,
    }

    override val world = World(Layer.entries.toTypedArray())

    init {
        // GameActivity 에서 기준 좌표계를 3200x1800 으로 잡았고,
        // desert.tmj 는 32x18 tile map 이므로 tile 하나를 100x100 으로 그리면 화면을 정확히 채운다.
        world.add(
            TiledBackground(gctx, "map/desert.tmj", tileWidth = 100f, tileHeight = 100f),
            Layer.BG,
        )
    }
}
