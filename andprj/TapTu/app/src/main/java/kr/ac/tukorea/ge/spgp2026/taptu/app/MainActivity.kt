package kr.ac.tukorea.ge.spgp2026.taptu.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.data.SongLoader
import kr.ac.tukorea.ge.spgp2026.taptu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val songs = SongLoader.load(assets)
        Log.d(javaClass.simpleName, "loaded ${songs.size} songs")
        songs.take(3).forEach { song ->
            Log.d(javaClass.simpleName, "$song")
        }
    }

    fun onBtnSong(view: View) {
        val songIndex = when (view.id) {
            R.id.songButton_0 -> 0
            R.id.songButton_1 -> 1
            else -> 2
        }

        startGameActivity(songIndex)
    }

    private fun startGameActivity(songIndex: Int) {
        val intent = Intent(this, MainGameActivity::class.java)
        intent.putExtra(MainGameActivity.EXTRAS_SONG_INDEX, songIndex)
        startActivity(intent)
    }
}
