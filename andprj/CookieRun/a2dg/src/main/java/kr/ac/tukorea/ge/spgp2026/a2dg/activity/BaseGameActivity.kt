package kr.ac.tukorea.ge.spgp2026.a2dg.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameView

abstract class BaseGameActivity : AppCompatActivity() {
    protected lateinit var gameView: GameView

    // App code chooses the root scene.
    protected abstract fun createRootScene(gctx: GameContext): Scene

    // App code injects debug flags so a2dg does not depend on app BuildConfig.
    protected open val drawsDebugGrid: Boolean = false
    protected open val drawsDebugInfo: Boolean = false
    protected open val drawsFpsGraph: Boolean = false

    // deprecated 된 onBackPressed() override 대신
    // OnBackPressedCallback 을 멤버로 두고 dispatcher 에 등록해 뒤로 가기 이벤트를 처리한다.
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!gameView.onBackPressed()) {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GameView.drawsDebugGrid = drawsDebugGrid
        GameView.drawsDebugInfo = drawsDebugInfo
        GameView.drawsFpsGraph = drawsFpsGraph
        gameView = GameView(this)
        gameView.setRootScene(::createRootScene)
        setContentView(gameView)
        setFullScreen()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        // #3 방식으로 쓴다면 멤버 프로퍼티 없이 여기서 바로 만들 수도 있다.
        // onCreate() 가 길어지는 대신 관련 코드가 한곳에 모인다.
        //
        // onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        //     override fun handleOnBackPressed() {
        //         if (!gameView.onBackPressed()) {
        //             isEnabled = false
        //             onBackPressedDispatcher.onBackPressed()
        //         }
        //     }
        // })
    }

    override fun onPause() {
        gameView.pauseGame()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        gameView.resumeGame()
    }

    override fun onDestroy() {
        gameView.destroyGame()
        super.onDestroy()
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
