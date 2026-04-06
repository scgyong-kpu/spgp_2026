package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.spgp2026.dragonflight.databinding.ActivityMainBinding
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(binding.root)

        // 디버그 빌드일 때에는 1초 후 게임 화면으로 바로 넘어가게 한다
        if (BuildConfig.DEBUG) {
            Handler(Looper.getMainLooper()).postDelayed({
                startGameActivity()
            }, 1000)
        }
    }

    fun onStartGameClicked(view: View) {
        startGameActivity()
    }

    private fun startGameActivity() {
        Log.d(javaClass.simpleName, "Start Game")
        val intent = Intent(this, DragonFlightActivity::class.java)
        startActivity(intent)
    }
}