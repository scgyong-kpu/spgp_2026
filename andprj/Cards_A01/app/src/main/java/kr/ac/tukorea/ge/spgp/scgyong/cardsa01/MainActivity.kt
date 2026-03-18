package kr.ac.tukorea.ge.spgp.scgyong.cardsa01

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp.scgyong.cardsa01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onCardButtonClick(view: View) {
//        val name = resources.getResourceName(view.id)
//        val msg = "Clicked: $name(${view.id})"
        val msg = getString(R.string.card_clicked_fmt, view.id)
        Log.d("MainActivity", msg)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}