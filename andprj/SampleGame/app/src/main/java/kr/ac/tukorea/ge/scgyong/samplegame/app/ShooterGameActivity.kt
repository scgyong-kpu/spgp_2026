package kr.ac.tukorea.ge.scgyong.samplegame.app

import kr.ac.tukorea.ge.scgyong.samplegame.BuildConfig
import kr.ac.tukorea.ge.scgyong.samplegame.game.scene.main.MainScene
import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class ShooterGameActivity : BaseGameActivity() {
    // BuildConfig stays in the app module.
    override val drawsDebugGrid = BuildConfig.DRAWS_DEBUG_GRID
    override val drawsDebugInfo = BuildConfig.DRAWS_DEBUG_INFO
    override val drawsFpsGraph = BuildConfig.DRAWS_FPS_GRAPH

    // The app decides which scene becomes the root scene.
    override fun createRootScene(gctx: GameContext): Scene {
        //gctx.metrics.setSize(1500f, 1200f)
        return MainScene(gctx)
    }
}
