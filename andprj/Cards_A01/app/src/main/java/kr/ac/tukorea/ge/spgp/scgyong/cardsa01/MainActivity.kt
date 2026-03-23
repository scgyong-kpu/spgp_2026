package kr.ac.tukorea.ge.spgp.scgyong.cardsa01

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.spgp.scgyong.cardsa01.databinding.ActivityMainBinding
import androidx.core.view.isInvisible

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

    private val imageResIds = arrayOf(
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd,
    )

    private var visibleCardCount = 0
    private var flipCount = 0
        set(value) {
            field = value
            val msg = getString(R.string.flip_count_fmt, value)
            binding.flipCountTextView.text = msg
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                handleCardClick(index)
            }
        }

        startNewGame()
    }

    fun handleCardClick(buttonIndex: Int) {
        if (buttonIndex == openedCardIndex) {
            return
        }
        val button = buttons[buttonIndex]

        val imgResId = imageResIds[buttonIndex]
        button.setImageResource(imgResId)

        openedCardIndex?.let { index ->
            val openedResId = imageResIds[index]
            val openedButton = buttons[index]
            if (imgResId == openedResId) {
                openedButton.visibility = View.INVISIBLE
                button.visibility = View.INVISIBLE
                openedCardIndex = null
                visibleCardCount -= 2
                if (visibleCardCount == 0) {
                    showRestartDialog(R.string.gameover, R.string.restart_one_more_game)
                }
                return
            } else {
                openedButton.setImageResource(R.mipmap.card_blue_back)
            }

        }
        flipCount++
        openedCardIndex = buttonIndex
    }

    fun onRestartButtonClicked(view: View) {
        showRestartDialog(R.string.restart_dlg_title, R.string.restart_dlg_message)
    }

    private fun showRestartDialog(titleResId: Int, messageResId: Int) {
        AlertDialog.Builder(this)
            .setTitle(titleResId)
            .setMessage(messageResId)
            .setPositiveButton(R.string.yes) { _, _ ->
                startNewGame()
            }
            .setNegativeButton(R.string.no, null)
            //.create()
            .show()
    }

    private fun startNewGame() {
        // imageResIds.shuffle()
        flipCount = 0
        visibleCardCount = imageResIds.size

        buttons.forEachIndexed { index, button ->
            button.setImageResource(R.mipmap.card_blue_back)
            button.visibility = View.VISIBLE
        }
    }
}