package kr.ac.tukorea.ge.spgp2026.taptu.app

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val songIndex = intent.extras?.getInt(EXTRAS_SONG_INDEX) ?: 0
        Log.d(javaClass.simpleName, "song index = $songIndex")
        val textView = TextView(this)
        textView.text = "Song #$songIndex"
        textView.textSize = 30f
        textView.gravity = android.view.Gravity.CENTER
        setContentView(textView)
    }

    companion object {
        const val EXTRAS_SONG_INDEX = "songIndex"
    }
}
