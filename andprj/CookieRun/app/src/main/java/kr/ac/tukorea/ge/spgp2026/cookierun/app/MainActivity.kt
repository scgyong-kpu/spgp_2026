package kr.ac.tukorea.ge.spgp2026.cookierun.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp2026.cookierun.BuildConfig
import kr.ac.tukorea.ge.spgp2026.cookierun.R
import kr.ac.tukorea.ge.spgp2026.cookierun.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var stage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStage(1)

        // 디버그 빌드일 때에는 1초 후 게임 화면으로 바로 넘어가게 한다
        if (BuildConfig.DEBUG) {
            Handler(Looper.getMainLooper()).postDelayed({
                startGameActivity()
            }, 1000)
        }
    }

    fun onBtnStartGame(view: View) {
        startGameActivity()
    }

    fun onBtnPreviousStage(view: View) {
        setStage(stage - 1)
    }

    fun onBtnNextStage(view: View) {
        setStage(stage + 1)
    }

    private fun startGameActivity() {
        val intent = Intent(this, CookieRunActivity::class.java)
        intent.putExtra(CookieRunActivity.KEY_STAGE, stage)
        startActivity(intent)
    }

    private fun setStage(stage: Int) {
        this.stage = stage.coerceIn(1, STAGE_COUNT)
        binding.stageTextView.text = getString(R.string.title_stage_fmt, this.stage)
        binding.prevStageButton.isEnabled = this.stage > 1
        binding.nextStageButton.isEnabled = this.stage < STAGE_COUNT
    }

    companion object {
        private const val STAGE_COUNT = 3
    }
}
