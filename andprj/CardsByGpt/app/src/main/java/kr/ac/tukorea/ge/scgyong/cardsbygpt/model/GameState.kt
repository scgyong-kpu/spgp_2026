package kr.ac.tukorea.ge.scgyong.cardsbygpt.model

data class CardState(
    val cardIndex: Int,   // 0..7
    var isOpen: Boolean = false,
    var isMatched: Boolean = false
)

enum class SelectResult {
    FIRST_OPENED,
    MATCHED,
    MISMATCHED,
    ALREADY_OPEN,
    ALREADY_MATCHED
}

class GameState(
    private val cardTypeCount: Int = 8
) {
    val cards = mutableListOf<CardState>()

    var openedIndex: Int? = null
        private set

    var flipCount: Int = 0
        private set

    init {
        reset()
    }

    fun reset(shuffle: Boolean = true) {
        cards.clear()

        for (cardIndex in 0 until cardTypeCount) {
            cards.add(CardState(cardIndex))
            cards.add(CardState(cardIndex))
        }

        if (shuffle) {
            cards.shuffle()
        }

        openedIndex = null
        flipCount = 0
    }

    fun selectCard(index: Int): SelectResult {
        val card = cards[index]

        if (card.isMatched) {
            return SelectResult.ALREADY_MATCHED
        }

        if (card.isOpen) {
            return SelectResult.ALREADY_OPEN
        }

        card.isOpen = true
        flipCount++

        val prevIndex = openedIndex

        if (prevIndex == null) {
            openedIndex = index
            return SelectResult.FIRST_OPENED
        }

        val prevCard = cards[prevIndex]

        return if (prevCard.cardIndex == card.cardIndex) {
            prevCard.isMatched = true
            card.isMatched = true
            openedIndex = null
            SelectResult.MATCHED
        } else {
            prevCard.isOpen = false
            openedIndex = index
            SelectResult.MISMATCHED
        }
    }

    fun isCleared(): Boolean {
        return cards.all { it.isMatched }
    }
}