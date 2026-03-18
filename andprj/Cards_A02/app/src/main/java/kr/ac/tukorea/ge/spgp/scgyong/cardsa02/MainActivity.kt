package kr.ac.tukorea.ge.spgp.scgyong.cardsa02

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp.scgyong.cardsa02.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var openedIndex: Int? = null
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
    }

    fun onCardClicked(view: View) {
        val msg = getString(R.string.card_clicked_fmt, view.id)
        Log.d("MainActivity", msg)
        val button = view as ImageButton
        val buttonIndex = buttons.indexOf(button)

        button.setImageResource(R.mipmap.card_as)
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

//        if (openedIndex != null) {
//            buttons[openedIndex!!].setImageResource(R.mipmap.card_blue_back)
//        }

        openedIndex?.let { index ->
            buttons[index].setImageResource(R.mipmap.card_blue_back)
        }
        openedIndex = buttonIndex
    }

}