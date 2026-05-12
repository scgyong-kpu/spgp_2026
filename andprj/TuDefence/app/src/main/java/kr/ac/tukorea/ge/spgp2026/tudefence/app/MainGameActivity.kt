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

class MainGameActivity : BaseGameActivity() {
    override val drawsDebugGrid: Boolean = BuildConfig.DEBUG
    override val drawsDebugInfo: Boolean = BuildConfig.DEBUG
    override val drawsFpsGraph: Boolean = BuildConfig.DEBUG
    override fun createRootScene(gctx: GameContext): Scene {
        // 게임의 가로 세로 크기를 3200x1800으로 설정. 타일을 32개x18개로 배치할 수 있다.
        gctx.metrics.setSize(3200f, 1800f)
        return object:Scene(gctx) {}
    }
}