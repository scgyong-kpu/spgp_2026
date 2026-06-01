package kr.ac.tukorea.ge.spgp2026.taptu.app

import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.BuildConfig
import kr.ac.tukorea.ge.spgp2026.taptu.game.scene.main.MainScene

class MainGameActivity : BaseGameActivity() {
//    override val drawsDebugGrid = BuildConfig.DEBUG
    override val drawsDebugInfo = BuildConfig.DEBUG
    override val drawsFpsGraph = BuildConfig.DEBUG

    override fun createRootScene(gctx: GameContext): Scene {
        val songIndex = intent.extras?.getInt(EXTRAS_SONG_INDEX) ?: 0
        return MainScene(gctx, songIndex)
    }

    companion object {
        const val EXTRAS_SONG_INDEX = "songIndex"
    }
}
