package kr.ac.tukorea.ge.spgp2026.tudefence.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.BuildConfig
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main.MainScene

class MainGameActivity : BaseGameActivity() {
    override val drawsDebugGrid: Boolean = BuildConfig.DEBUG
    override val drawsDebugInfo: Boolean = BuildConfig.DEBUG
    override val drawsFpsGraph: Boolean = BuildConfig.DEBUG

    override fun createRootScene(gctx: GameContext): Scene {
        // 게임의 가로 세로 크기를 1600x900 으로 설정한다.
        // stage map 은 32x18 map 이므로 tile 하나를 50x50 으로 그리면 화면을 정확히 채운다.
        gctx.metrics.setSize(1600f, 900f)
        val stage = intent.getIntExtra(EXTRA_STAGE, DEFAULT_STAGE).coerceIn(MIN_STAGE, MAX_STAGE)
        return MainScene(gctx, stage)
    }

    companion object {
        const val EXTRA_STAGE = "stage"
        private const val DEFAULT_STAGE = 1
        private const val MIN_STAGE = 1
        private const val MAX_STAGE = 3
    }
}
