package kr.ac.tukorea.ge.scgyong.samplegame.app

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.GameView
import kr.ac.tukorea.ge.spgp2026.a2dg.Scene

abstract class BaseGameActivity : AppCompatActivity() {
    protected lateinit var gameView: GameView

    // App code chooses the root scene.
    protected abstract fun createRootScene(gctx: GameContext): Scene

    // App code injects debug flags so a2dg does not depend on app BuildConfig.
    protected open val drawsDebugGrid: Boolean = false
    protected open val drawsDebugInfo: Boolean = false
    protected open val drawsFpsGraph: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GameView.drawsDebugGrid = drawsDebugGrid
        GameView.drawsDebugInfo = drawsDebugInfo
        GameView.drawsFpsGraph = drawsFpsGraph
        gameView = GameView(this)
        gameView.setRootScene(::createRootScene)
        setContentView(gameView)
        setFullScreen()
    }

    @Suppress("DEPRECATION")
    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = window.insetsController
            if (insetsController != null) {
                insetsController.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.hide(WindowInsets.Type.systemBars())
            }
        } else {
            val flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            gameView.systemUiVisibility = flags
        }
    }
}
