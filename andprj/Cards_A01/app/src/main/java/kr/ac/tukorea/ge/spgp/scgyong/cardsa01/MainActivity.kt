package kr.ac.tukorea.ge.spgp.scgyong.cardsa01

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp.scgyong.cardsa01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var previousImageButton: ImageButton? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onCardButtonClick(view: View) {
        //val button = view as? ImageButton ?: return
        val button = view as ImageButton

        button.setImageResource(R.mipmap.card_as)

        previousImageButton?.setImageResource(R.mipmap.card_blue_back)
        previousImageButton = button
    }
}