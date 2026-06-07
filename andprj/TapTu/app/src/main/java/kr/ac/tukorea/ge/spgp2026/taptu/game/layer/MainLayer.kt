package kr.ac.tukorea.ge.spgp2026.taptu.game.layer

import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

enum class MainLayer {
    BG,
    EXPLOSION,
    NOTE,
    UI,
    CONTROLLER,
}

fun GameContext.mainWorld(): World<MainLayer> {
    val world = scene.world as World<MainLayer>
    return world
}