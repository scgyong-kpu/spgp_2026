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
        val msg = getString(R.string.card_clicked_fmt, view.id)
        Log.d("MainActivity", msg)
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}