package kr.ac.tukorea.ge.spgp2026.taptu.app

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.spgp2026.taptu.R

class MainGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val songIndex = intent.extras?.getInt(EXTRAS_SONG_INDEX) ?: 0
        Log.d(javaClass.simpleName, "song index = $songIndex")
    }

    companion object {
        const val EXTRAS_SONG_INDEX = "songIndex"
    }
}
