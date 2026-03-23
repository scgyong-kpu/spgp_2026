package kr.ac.tukorea.ge.scgyong.morecontrols

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.morecontrols.databinding.ActivityAnotherBinding

class AnotherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnotherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnotherBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}