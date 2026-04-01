package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {
    // 지금은 Player 하나만 있으므로 layer 도 하나뿐이지만,
    // enum 으로 바꿔 두면 나중에 BULLET, ENEMY, EFFECT 같은 layer 를
    // 이름으로 더 읽기 쉽게 추가할 수 있다.
    enum class Layer {
        PLAYER,
    }

    val player = Player(gctx)

    override val world = World(arrayOf(Layer.PLAYER)).apply {
        add(player, Layer.PLAYER)
    }
}
