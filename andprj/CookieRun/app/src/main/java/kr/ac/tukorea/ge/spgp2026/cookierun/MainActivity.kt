package kr.ac.tukorea.ge.spgp2026.cookierun

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.spgp2026.cookierun.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

    private fun startGameActivity() {
        val intent = Intent(this, CookieRunActivity::class.java)
        startActivity(intent)
    }
}
