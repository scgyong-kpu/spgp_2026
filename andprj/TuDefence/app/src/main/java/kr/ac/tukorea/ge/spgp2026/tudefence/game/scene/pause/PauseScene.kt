package kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.pause

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.DrawableSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R

class PauseScene(gctx: GameContext): Scene(gctx) {
    enum class Layer {
        UI,
    }

    override val isTransparent = true
    override val world = World(Layer.entries.toTypedArray())

    init {
        val speechBox = DrawableSprite(gctx.res.getDrawable(R.drawable.speech_box)).apply {
            setSize(SPEECH_BOX_WIDTH, SPEECH_BOX_HEIGHT)
            setCenter(gctx.metrics.width / 2f, gctx.metrics.height / 2f)
        }
        world.add(speechBox, Layer.UI)
    }

    companion object {
        private const val SPEECH_BOX_WIDTH = 800f
        private const val SPEECH_BOX_HEIGHT = 600f
    }
}
