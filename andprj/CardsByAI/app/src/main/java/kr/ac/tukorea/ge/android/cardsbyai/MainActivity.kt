package kr.ac.tukorea.ge.android.cardsbyai

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.android.cardsbyai.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cardImageViews: List<ImageView>
    private lateinit var gameState: GameState
    private val totalCards = 16

    private companion object {
        const val NO_CARD_SELECTED = -1
        const val FLIP_ANIMATION_DURATION = 100L
        const val HIDE_SHOW_ANIMATION_DURATION = 300L
        const val CHECK_CARDS_DELAY = 400L
    }

    private val cardImages = listOf(
        R.mipmap.card_as,
        R.mipmap.card_2c,
        R.mipmap.card_3d,
        R.mipmap.card_4h,
        R.mipmap.card_5s,
        R.mipmap.card_jc,
        R.mipmap.card_kd,
        R.mipmap.card_qh
    )

    private val cardBack = R.mipmap.card_blue_back

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Try to restore from SharedPreferences first, then Bundle, then create new
        gameState = GameState.load(this) ?: (savedInstanceState?.getParcelable("gameState", GameState::class.java) ?: GameState.newGame())
        setupUI()

        binding.restartButton.setOnClickListener {
            showRestartConfirmDialog()
        }
    }

    private fun setupUI() {
        setupCardImageViews()
        
        // Apply current game state to card views
        cardImageViews.forEachIndexed { index, imageView ->
            val card = gameState.cardList[index]
            when {
                card.isRemoved -> imageView.visibility = ImageView.INVISIBLE
                card.isOpen -> imageView.setImageResource(cardImages[card.cardType])
                else -> imageView.setImageResource(cardBack)
            }
        }
        
        updateFlipCountText()
    }

    private fun initializeGame() {
        gameState = GameState.newGame()
        gameState.clear(this)
        setupUI()
    }

    private fun setupCardImageViews() {
        // Get all card ImageViews
        cardImageViews = listOf(
            binding.cardImageView00, binding.cardImageView01, binding.cardImageView02, binding.cardImageView03,
            binding.cardImageView10, binding.cardImageView11, binding.cardImageView12, binding.cardImageView13,
            binding.cardImageView20, binding.cardImageView21, binding.cardImageView22, binding.cardImageView23,
            binding.cardImageView30, binding.cardImageView31, binding.cardImageView32, binding.cardImageView33
        )

        // Reset and setup card views
        cardImageViews.forEachIndexed { index, imageView ->
            imageView.scaleX = 1f
            imageView.scaleY = 1f
            imageView.rotation = 0f
            imageView.visibility = ImageView.VISIBLE
            imageView.setImageResource(cardBack)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("gameState", gameState)
    }

    override fun onPause() {
        super.onPause()
        gameState.save(this)
    }

    fun onCardClick(view: View) {
        val position = cardImageViews.indexOf(view as ImageView)
        if (position < 0) return
        
        onCardClickInternal(position)
    }

    private fun onCardClickInternal(position: Int) {
        val card = gameState.cardList[position]

        // If already removed
        if (card.isRemoved) {
            return
        }

        // If already open
        if (card.isOpen) {
            Toast.makeText(this, getString(R.string.card_already_open), Toast.LENGTH_SHORT).show()
            return
        }

        // If already checking two cards
        if (gameState.isCheckingCards) {
            return
        }

        card.isOpen = true
        gameState.flipCount++
        updateFlipCountText()
        
        // Show flip animation
        flipCard(cardImageViews[position], cardImages[card.cardType]) {
            if (gameState.firstCardIndex == NO_CARD_SELECTED) {
                // First card
                gameState.firstCardIndex = position
            } else if (gameState.secondCardIndex == NO_CARD_SELECTED) {
                // Second card
                gameState.secondCardIndex = position
                gameState.isCheckingCards = true

                // Check if cards match after a short delay
                cardImageViews[position].postDelayed({
                    checkCards()
                }, CHECK_CARDS_DELAY)
            }
        }
    }

    private fun flipCard(imageView: ImageView, cardImageResource: Int, onEnd: (() -> Unit)? = null) {
        imageView.animate()
            .scaleX(0f)
            .setDuration(FLIP_ANIMATION_DURATION)
            .withEndAction {
                imageView.setImageResource(cardImageResource)
                imageView.animate()
                    .scaleX(1f)
                    .setDuration(FLIP_ANIMATION_DURATION)
                    .withEndAction { onEnd?.invoke() }
                    .start()
            }
            .start()
    }

    private fun hideOrShow(show: Boolean, card: ImageView, onEnd: (() -> Unit)? = null) {
        if (show) {
            card.scaleX = 0f
            card.scaleY = 0f
            card.rotation = 3600.0f
            card.visibility = ImageView.VISIBLE
            card.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(0f)
                .setDuration(HIDE_SHOW_ANIMATION_DURATION)
                .withEndAction { onEnd?.invoke() }
                .start()
        } else {
            card.rotation = 0.0f
            card.animate()
                .scaleX(0f)
                .scaleY(0f)
                .rotation(3600.0f)
                .setDuration(HIDE_SHOW_ANIMATION_DURATION)
                .withEndAction { 
                    card.scaleX = 1f
                    card.scaleY = 1f
                    card.rotation = 0f
                    card.visibility = ImageView.INVISIBLE
                    onEnd?.invoke()
                }
                .start()
        }
    }

    private fun checkCards() {
        val cardsMatch = gameState.cardList[gameState.firstCardIndex].cardType == gameState.cardList[gameState.secondCardIndex].cardType
        
        if (cardsMatch) {
            hideCards()
            markCardsAsRemoved()
            if (gameState.removedCount == totalCards) gameCompleted()
            resetCardSelection()
        } else {
            hideCards { resetCards() }
        }
    }

    private fun hideCards(onEnd: (() -> Unit)? = null) {
        val firstImageView = cardImageViews[gameState.firstCardIndex]
        val secondImageView = cardImageViews[gameState.secondCardIndex]
        hideOrShow(false, firstImageView)
        hideOrShow(false, secondImageView, onEnd)
    }

    private fun markCardsAsRemoved() {
        gameState.cardList[gameState.firstCardIndex].isRemoved = true
        gameState.cardList[gameState.secondCardIndex].isRemoved = true
        gameState.removedCount += 2
    }

    private fun resetCards() {
        val firstCard = gameState.cardList[gameState.firstCardIndex]
        val secondCard = gameState.cardList[gameState.secondCardIndex]
        val firstImageView = cardImageViews[gameState.firstCardIndex]
        val secondImageView = cardImageViews[gameState.secondCardIndex]

        firstCard.isOpen = false
        secondCard.isOpen = false
        firstImageView.setImageResource(cardBack)
        secondImageView.setImageResource(cardBack)
        
        hideOrShow(true, firstImageView)
        hideOrShow(true, secondImageView)
        
        resetCardSelection()
    }

    private fun resetCardSelection() {
        gameState.isCheckingCards = false
        gameState.firstCardIndex = NO_CARD_SELECTED
        gameState.secondCardIndex = NO_CARD_SELECTED
    }

    private fun updateFlipCountText() {
        binding.flipCountText.text = getString(R.string.flip_count, gameState.flipCount)
    }

    private fun showConfirmDialog(titleId: Int, messageId: Int, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(getString(titleId))
            .setMessage(getString(messageId))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun showRestartConfirmDialog() {
        showConfirmDialog(R.string.restart, R.string.restart_confirm, ::initializeGame)
    }

    private fun gameCompleted() {
        showConfirmDialog(R.string.restart, R.string.play_again, ::initializeGame)
    }
}
