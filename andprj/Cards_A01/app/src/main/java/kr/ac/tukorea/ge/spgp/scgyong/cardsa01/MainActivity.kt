package kr.ac.tukorea.ge.spgp.scgyong.cardsa01

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp.scgyong.cardsa01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var openedCardIndex: Int? = null
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val buttons by lazy {
        arrayOf(
            binding.card00, binding.card01, binding.card02, binding.card03,
            binding.card10, binding.card11, binding.card12, binding.card13,
            binding.card20, binding.card21, binding.card22, binding.card23,
            binding.card30, binding.card31, binding.card32, binding.card33,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                onCardButtonClick(index)
            }
        }
    }

    fun onCardButtonClick(buttonIndex: Int) {
        val button = buttons[buttonIndex]

        button.setImageResource(R.mipmap.card_as)

        openedCardIndex?.let { index ->
            buttons[index].setImageResource(R.mipmap.card_blue_back)
        }
//        if (openedCardIndex != null) {
//            val openedButton = buttons[openedCardIndex!!]
//            openedButton.setImageResource(R.mipmap.card_blue_back)
//        }
        openedCardIndex = buttonIndex
    }
}