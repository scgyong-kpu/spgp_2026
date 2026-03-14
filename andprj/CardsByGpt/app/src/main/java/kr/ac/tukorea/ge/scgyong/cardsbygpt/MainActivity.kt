package kr.ac.tukorea.ge.scgyong.cardsbygpt

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.cardsbygpt.databinding.ActivityMainBinding
import kr.ac.tukorea.ge.scgyong.cardsbygpt.model.GameState
import kr.ac.tukorea.ge.scgyong.cardsbygpt.model.SelectResult

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cardButtons: Array<ImageButton>
    private lateinit var gameState: GameState

    private val cardImageIds = arrayOf(
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameState = GameState()

        initCardButtons()
        startGame()
    }

    private fun initCardButtons() {
        cardButtons = arrayOf(
            binding.card0, binding.card1, binding.card2, binding.card3,
            binding.card4, binding.card5, binding.card6, binding.card7,
            binding.card8, binding.card9, binding.card10, binding.card11,
            binding.card12, binding.card13, binding.card14, binding.card15
        )

        for (i in cardButtons.indices) {
            cardButtons[i].setOnClickListener {
                handleCardClick(i)
            }
        }
    }

    private fun startGame() {
        gameState.reset()

        for (button in cardButtons) {
            button.visibility = View.VISIBLE
            button.setImageResource(R.mipmap.card_blue_back)
        }

        updateFlipCount()
    }

    private fun handleCardClick(index: Int) {
        val first = gameState.openedIndex
        val result = gameState.selectCard(index)

        when (result) {

            SelectResult.FIRST_OPENED -> {
                showFront(index)
                updateFlipCount()
            }

            SelectResult.MATCHED -> {
                showFront(index)
                removeCard(first!!)
                removeCard(index)

                updateFlipCount()

                if (gameState.isCleared()) {
                    showRestartDialog(
                        R.string.clear_title,
                        R.string.clear_message
                    )
                }
            }

            SelectResult.MISMATCHED -> {
                showBack(first!!)
                showFront(index)
                updateFlipCount()
            }

            SelectResult.ALREADY_OPEN -> {
                Toast.makeText(
                    this,
                    R.string.already_open_card,
                    Toast.LENGTH_SHORT
                ).show()
            }

            SelectResult.ALREADY_MATCHED -> {
                // ignore
            }
        }
    }

    private fun showFront(index: Int) {
        val cardIndex = gameState.cards[index].cardIndex
        val resId = cardImageIds[cardIndex]
        cardButtons[index].setImageResource(resId)
    }

    private fun showBack(index: Int) {
        cardButtons[index].setImageResource(R.mipmap.card_blue_back)
    }

    private fun removeCard(index: Int) {
        cardButtons[index].visibility = View.INVISIBLE
    }

    private fun updateFlipCount() {
        binding.flipCountText.text =
            getString(R.string.flip_count_format, gameState.flipCount)
    }

    fun onRestartClicked(view: View) {
        showRestartDialog(
            R.string.restart_title,
            R.string.restart_message
        )
    }

    private fun showRestartDialog(
        @StringRes titleResId: Int,
        @StringRes messageResId: Int
    ) {
        AlertDialog.Builder(this)
            .setTitle(titleResId)
            .setMessage(messageResId)
            .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                startGame()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
}
