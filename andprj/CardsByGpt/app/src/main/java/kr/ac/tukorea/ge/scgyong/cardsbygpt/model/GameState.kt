package kr.ac.tukorea.ge.scgyong.cardsbygpt.model

import org.json.JSONArray
import org.json.JSONObject

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

    fun toJson(): String {
        // 현재 게임 상태를 JSON 문자열로 직렬화해 Bundle이나 SharedPreferences에 저장할 수 있게 만든다.
        val root = JSONObject()

        root.put(KEY_VERSION, JSON_VERSION)
        root.put(KEY_FLIP_COUNT, flipCount)

        // 열린 카드가 없으면 JSON null로 저장한다.
        if (openedIndex == null) {
            root.put(KEY_OPENED_INDEX, JSONObject.NULL)
        } else {
            root.put(KEY_OPENED_INDEX, openedIndex)
        }

        // 카드 목록은 배열로 저장하고, 기본값인 isOpen=false는 따로 저장하지 않는다.
        val cardsArray = JSONArray()
        for (card in cards) {
            val cardObject = JSONObject()
            cardObject.put(KEY_CARD_INDEX, card.cardIndex)

            // false는 기본값으로 보고 matched=true일 때만 저장한다.
            if (card.isMatched) {
                cardObject.put(KEY_MATCHED, true)
            }

            cardsArray.put(cardObject)
        }

        root.put(KEY_CARDS, cardsArray)
        return root.toString()
    }

    companion object {
        // JSON 직렬화와 역직렬화에 사용할 키 이름들이다.
        private const val KEY_VERSION = "version"
        private const val KEY_FLIP_COUNT = "flipCount"
        private const val KEY_OPENED_INDEX = "openedIndex"
        private const val KEY_CARDS = "cards"
        private const val KEY_CARD_INDEX = "cardIndex"
        private const val KEY_MATCHED = "matched"

        // 저장 형식이 바뀔 경우를 구분하기 위한 버전 값이다.
        private const val JSON_VERSION = 1

        fun fromJson(json: String): GameState? {
            return try {
                // JSON 문자열이 현재 앱 버전이 이해할 수 있는 형식인지 먼저 검사한다.
                val root = JSONObject(json)

                val version = root.optInt(KEY_VERSION, -1)
                if (version != JSON_VERSION) {
                    return null
                }

                // 카드 배열이 없거나 장 수가 다르면 복원하지 않는다.
                val cardsArray = root.optJSONArray(KEY_CARDS) ?: return null
                if (cardsArray.length() != 16) {
                    return null
                }

                // 생성자에서 만든 기본 상태를 지우고 JSON에 저장된 카드 목록으로 다시 채운다.
                val state = GameState()
                state.cards.clear()

                for (i in 0 until cardsArray.length()) {
                    val cardObject = cardsArray.optJSONObject(i) ?: return null

                    // 카드 종류 인덱스가 범위를 벗어나면 잘못된 저장 데이터로 본다.
                    val cardIndex = cardObject.optInt(KEY_CARD_INDEX, -1)
                    if (cardIndex !in 0 until state.cardTypeCount) {
                        return null
                    }

                    val matched = cardObject.optBoolean(KEY_MATCHED, false)

                    state.cards.add(
                        CardState(
                            cardIndex = cardIndex,
                            isOpen = false,
                            isMatched = matched
                        )
                    )
                }

                // 뒤집은 횟수와 현재 열린 카드 위치를 함께 복원한다.
                state.flipCount = root.optInt(KEY_FLIP_COUNT, 0)

                state.openedIndex =
                    if (root.isNull(KEY_OPENED_INDEX)) null
                    else root.optInt(KEY_OPENED_INDEX, -1).takeIf { it in state.cards.indices }

                // openedIndex를 기준으로 isOpen 상태를 다시 맞춘다.
                for (card in state.cards) {
                    card.isOpen = false
                }
                state.openedIndex?.let { index ->
                    if (!state.cards[index].isMatched) {
                        state.cards[index].isOpen = true
                    } else {
                        state.openedIndex = null
                    }
                }

                state
            } catch (e: Exception) {
                // 저장 데이터가 깨졌거나 형식이 맞지 않으면 복원 실패로 처리한다.
                null
            }
        }
    }
}