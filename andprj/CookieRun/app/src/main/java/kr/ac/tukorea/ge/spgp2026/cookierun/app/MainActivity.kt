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
import kr.ac.tukorea.ge.spgp2026.cookierun.game.objs.player.CookieCatalog
import kr.ac.tukorea.ge.spgp2026.cookierun.game.objs.player.CookieInfo

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var stage = 1
    private lateinit var cookies: List<CookieInfo>
    private var cookieIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        cookies = CookieCatalog.all(this)
        setStage(1)
        setCookieIndex(0)

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

    fun onBtnPreviousCookie(view: View) {
        setCookieIndex(cookieIndex - 1)
    }

    fun onBtnNextCookie(view: View) {
        setCookieIndex(cookieIndex + 1)
    }

    private fun startGameActivity() {
        val intent = Intent(this, CookieRunActivity::class.java)
        intent.putExtra(CookieRunActivity.KEY_STAGE, stage)
        intent.putExtra(CookieRunActivity.KEY_COOKIE_ID, cookies[cookieIndex].id)
        startActivity(intent)
    }

    private fun setStage(stage: Int) {
        this.stage = stage.coerceIn(1, STAGE_COUNT)
        binding.stageTextView.text = getString(R.string.title_stage_fmt, this.stage)
        binding.prevStageButton.isEnabled = this.stage > 1
        binding.nextStageButton.isEnabled = this.stage < STAGE_COUNT
    }

    private fun setCookieIndex(index: Int) {
        cookieIndex = index.coerceIn(cookies.indices)
        val cookie = cookies[cookieIndex]
        binding.cookieNameTextView.text = cookie.name
        binding.cookieImageView.setImageBitmap(CookieCatalog.getBitmap(this, cookie.id, "icon"))
        binding.prevCookieButton.isEnabled = cookieIndex > 0
        binding.nextCookieButton.isEnabled = cookieIndex < cookies.lastIndex
    }

    companion object {
        private const val STAGE_COUNT = 3
    }
}
