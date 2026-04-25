package kr.ac.tukorea.ge.spgp2026.cookierun.app

import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class CookieRunActivity : BaseGameActivity() {
    override val drawsDebugGrid: Boolean = true
    override val drawsDebugInfo: Boolean = true
    override fun createRootScene(gctx: GameContext): Scene {
        return object : Scene(gctx) {
        }
    }
}