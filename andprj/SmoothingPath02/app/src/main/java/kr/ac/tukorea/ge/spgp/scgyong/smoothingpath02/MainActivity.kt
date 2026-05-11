package kr.ac.tukorea.ge.spgp.scgyong.smoothingpath02

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.spgp.scgyong.smoothingpath02.databinding.ActivityMainBinding

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
        binding.pathView.callback = object : PathView.Callback {
            override fun onSizeChange(size: Int) {
                updateCount(size)
            }
        }
        updateCount(0)
    }

    private fun updateCount(size: Int) {
        val text = getString(R.string.count_fmt, size)
        binding.countTextView.text = text
    }

    fun onBtnClear(view: View) {
        binding.pathView.clear()
    }

    fun onCheckClosed(view: View) {
        val checked = binding.closedCheckbox.isChecked
        binding.pathView.closed = checked
    }
}