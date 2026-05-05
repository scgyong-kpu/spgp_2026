package kr.ac.tukorea.ge.spgp2026.cookierun.app

import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.BuildConfig
import kr.ac.tukorea.ge.spgp2026.cookierun.game.main.MainScene

class CookieRunActivity : BaseGameActivity() {
    override val drawsDebugGrid: Boolean = BuildConfig.DEBUG
    override val drawsDebugInfo: Boolean = BuildConfig.DEBUG
    override val drawsFpsGraph: Boolean = BuildConfig.DEBUG

    override fun createRootScene(gctx: GameContext): Scene {
        gctx.metrics.setSize(1600f, 900f)
        val stage = intent.getIntExtra(KEY_STAGE, 1)
        return MainScene(gctx, stage)
    }

    companion object {
        const val KEY_STAGE = "stage"
        const val KEY_COOKIE_ID = "cookieId"
    }
}
