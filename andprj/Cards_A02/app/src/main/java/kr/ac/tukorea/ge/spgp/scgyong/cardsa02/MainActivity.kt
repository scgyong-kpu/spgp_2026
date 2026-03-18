package kr.ac.tukorea.ge.spgp.scgyong.cardsa02

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp.scgyong.cardsa02.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onCardClicked(view: View) {
        Log.d("MainActivity", "ImageButton Clicked! ID=${view.id}")
    }

}