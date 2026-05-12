package kr.ac.tukorea.ge.spgp2026.smoothingpath

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.spgp.scgyong.smoothingpath01.PathView
import kr.ac.tukorea.ge.spgp2026.smoothingpath.databinding.ActivityMainBinding
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
        binding.pathView.callback = object: PathView.Callback {
            override fun onSizeChanged(size: Int) {
                updateSize(size)
            }
        }
        updateSize(0)
    }
    private fun updateSize(size: Int) {
        val text = getString(R.string.count_fmt, size)
        binding.countTextView.text = text
    }

    fun onBtnClear(view: View) {
        binding.pathView.clear()
    }

    fun onCheckClosed(view: View) {
        binding.pathView.closed = binding.closedCheckbox.isChecked
    }


    fun onCheckCurved(view: View) {
        binding.pathView.curved = binding.curvedCheckbox.isChecked
    }

    fun onBtnStartAnimation(view: View) {
        binding.pathView.startPathAnimation()
    }
}