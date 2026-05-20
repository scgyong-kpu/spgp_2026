package kr.ac.tukorea.ge.spgp2026.tudefence.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.spgp2026.tudefence.BuildConfig
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var selectedStage = MIN_STAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.previousStageButton.setOnClickListener {
            selectedStage--
            if (selectedStage < MIN_STAGE) {
                selectedStage = MAX_STAGE
            }
            updateStageText()
        }
        binding.nextStageButton.setOnClickListener {
            selectedStage++
            if (selectedStage > MAX_STAGE) {
                selectedStage = MIN_STAGE
            }
            updateStageText()
        }
        binding.startGameButton.setOnClickListener {
            startGameActivity()
        }
        updateStageText()

        if (BuildConfig.DEBUG) {
            Handler(Looper.getMainLooper()).postDelayed(1000) {
                startGameActivity()
            }
        }
    }

    private fun startGameActivity() {
        val intent = Intent(this, MainGameActivity::class.java).apply {
            putExtra(MainGameActivity.EXTRA_STAGE, selectedStage)
        }
        startActivity(intent)
    }

    private fun updateStageText() {
        binding.stageTextView.text = "Stage $selectedStage"
    }

    companion object {
        private const val MIN_STAGE = 1
        private const val MAX_STAGE = 3
    }
}
