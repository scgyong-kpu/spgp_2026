package kr.ac.tukorea.ge.spgp.scgyong.cardsa02

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp.scgyong.cardsa02.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var openedIndex: Int? = null
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val imageResIds = arrayOf(
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd,
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd,
    )
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
                handleCardClick(index)
            }
        }
    }

    fun handleCardClick(buttonIndex: Int) {
        val msg = getString(R.string.card_clicked_fmt, buttonIndex)
        Log.d("MainActivity", msg)
        val button = buttons[buttonIndex]

        val imageResId = imageResIds[buttonIndex]
        button.setImageResource(imageResId)

        openedIndex?.let { index ->
            buttons[index].setImageResource(R.mipmap.card_blue_back)
        }
        openedIndex = buttonIndex
    }

}