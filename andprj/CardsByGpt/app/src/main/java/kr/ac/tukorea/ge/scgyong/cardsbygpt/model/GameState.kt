package kr.ac.tukorea.ge.scgyong.cardsbygpt.model

// 카드 한 장의 종류와 현재 상태를 표현한다.
data class CardState(
    val cardIndex: Int,   // 0..7
    var isOpen: Boolean = false,
    var isMatched: Boolean = false
)

// 카드 선택 결과를 Activity가 분기 처리할 수 있게 enum으로 정의한다.
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
    // 실제 게임에 사용되는 16장의 카드 상태 목록이다.
    val cards = mutableListOf<CardState>()

    // 현재 열려 있는 카드의 위치를 저장한다. 열려 있는 카드가 없으면 null이다.
    var openedIndex: Int? = null
        private set

    // 사용자가 카드를 뒤집은 총 횟수이다.
    var flipCount: Int = 0
        private set

    init {
        // GameState가 생성되면 바로 새 게임 상태를 만든다.
        reset()
    }

    fun reset(shuffle: Boolean = true) {
        cards.clear()

        // 카드 종류별로 두 장씩 추가해서 짝이 있는 16장을 만든다.
        for (cardIndex in 0 until cardTypeCount) {
            cards.add(CardState(cardIndex))
            cards.add(CardState(cardIndex))
        }

        // 게임 시작 시에는 카드 순서를 섞는다.
        if (shuffle) {
            cards.shuffle()
        }

        // 진행 상태도 모두 처음 값으로 되돌린다.
        openedIndex = null
        flipCount = 0
    }

    fun selectCard(index: Int): SelectResult {
        val card = cards[index]

        // 이미 짝이 맞은 카드는 다시 선택할 수 없다.
        if (card.isMatched) {
            return SelectResult.ALREADY_MATCHED
        }

        // 이미 열려 있는 카드를 다시 누른 경우도 거부한다.
        if (card.isOpen) {
            return SelectResult.ALREADY_OPEN
        }

        // 선택된 카드를 열고 뒤집은 횟수를 증가시킨다.
        card.isOpen = true
        flipCount++

        val prevIndex = openedIndex

        // 아직 열려 있는 카드가 없다면 이번 카드가 첫 번째 카드가 된다.
        if (prevIndex == null) {
            openedIndex = index
            return SelectResult.FIRST_OPENED
        }

        val prevCard = cards[prevIndex]

        return if (prevCard.cardIndex == card.cardIndex) {
            // 두 카드의 종류가 같으면 둘 다 매칭 완료 상태로 바꾼다.
            prevCard.isMatched = true
            card.isMatched = true
            openedIndex = null
            SelectResult.MATCHED
        } else {
            // 다르면 이전 카드는 다시 닫고, 현재 카드를 새 기준 카드로 남긴다.
            prevCard.isOpen = false
            openedIndex = index
            SelectResult.MISMATCHED
        }
    }

    fun isCleared(): Boolean {
        // 모든 카드가 매칭되었는지 검사한다.
        return cards.all { it.isMatched }
    }
}