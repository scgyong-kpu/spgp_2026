package kr.ac.tukorea.ge.spgp.scgyong.cardsa02

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
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

    private var flipCount = 0
        set(value) {
            field = value
            val msg = getString(R.string.flips_count_fmt, value)
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
        if (buttonIndex == openedIndex) {
            return
        }

        val msg = getString(R.string.card_clicked_fmt, buttonIndex)
        Log.d("MainActivity", msg)
        val button = buttons[buttonIndex]

        val imageResId = imageResIds[buttonIndex]
        button.setImageResource(imageResId)

        openedIndex?.let { index ->
            val openedButton = buttons[index]
            val openedResId = imageResIds[index]

            if (imageResId == openedResId) {
                // 이전에 열려 있던 카드와 현재 클릭된 카드가 같은 이미지라면
                // 두 카드를 맞춘 것으로 간주하고 삭제를 시도해 본다
                openedButton.visibility = View.INVISIBLE
                button.visibility = View.INVISIBLE
                openedIndex = null

                if (isGameOver()) {
                    showRestartDialog(R.string.done, R.string.restart_one_more_game)
                }
                return
            } else {
                openedButton.setImageResource(R.mipmap.card_blue_back)
            }
        }
        flipCount++
        openedIndex = buttonIndex
    }

    private fun isGameOver(): Boolean {
        return buttons.all { b -> b.visibility == View.INVISIBLE }
    }

    fun onRestartButtonClicked(view: View) {
        showRestartDialog(R.string.restart, R.string.restart_dialog_message)
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
        //imageResIds.shuffle()
        flipCount = 0

        buttons.forEachIndexed { index, button ->
            button.setImageResource(R.mipmap.card_blue_back)
            button.visibility = View.VISIBLE
        }
    }

}