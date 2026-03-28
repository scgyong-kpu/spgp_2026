package kr.ac.tukorea.ge.scgyong.samplegame.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.samplegame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        startGameActivity()
    }

    fun onStartGameClicked(view: View) {
        startGameActivity()
    }

    private fun startGameActivity() {
        Log.d(javaClass.simpleName, "Start Game")
        val intent = Intent(this, ShooterGameActivity::class.java)
        startActivity(intent)
    }
}
