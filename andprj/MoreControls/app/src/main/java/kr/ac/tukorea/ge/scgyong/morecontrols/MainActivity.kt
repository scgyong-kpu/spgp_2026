package kr.ac.tukorea.ge.scgyong.morecontrols

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.morecontrols.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onDoItButtonClick(view: View) {
        if (binding.goodProgrammerCheckbox.isChecked) {
            binding.mainTextView.setText(R.string.you_get_one_grand)
        } else {
            binding.mainTextView.setText(R.string.you_have_nothing)
        }
    }
}
