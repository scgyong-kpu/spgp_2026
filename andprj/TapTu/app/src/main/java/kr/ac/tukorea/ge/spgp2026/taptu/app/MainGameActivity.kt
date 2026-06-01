package kr.ac.tukorea.ge.spgp2026.taptu.app

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp2026.taptu.data.SongCatalog

class MainGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val songIndex = intent.extras?.getInt(EXTRAS_SONG_INDEX) ?: 0
        Log.d(javaClass.simpleName, "song index = $songIndex")
        SongCatalog.load(assets)
        val song = SongCatalog.songs.getOrNull(songIndex)
        val textView = TextView(this)
        textView.text = "Song #$songIndex\n${song ?: "Unknown song"}"
        textView.textSize = 30f
        textView.gravity = android.view.Gravity.CENTER
        setContentView(textView)
    }

    companion object {
        const val EXTRAS_SONG_INDEX = "songIndex"
    }
}
