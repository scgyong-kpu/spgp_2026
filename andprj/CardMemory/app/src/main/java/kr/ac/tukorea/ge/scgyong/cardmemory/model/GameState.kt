package kr.ac.tukorea.ge.scgyong.cardmemory.model

import org.json.JSONArray
import org.json.JSONObject

// 카드 선택 결과를 Activity가 분기 처리할 수 있게 enum으로 정의한다.
enum class SelectResult {
    FIRST_OPENED,
    MATCHED,
    MISMATCHED,
    ALREADY_OPEN,
    ALREADY_MATCHED
}

class GameState(val cardTypeCount: Int) {
    var cardIndices: Array<Int?> = Array(cardTypeCount * 2) { index -> index / 2 }
    var openedCardIndex: Int? = null
    var flipCount: Int = 0

    // 새 게임이 시작될 때 모델 상태 전체를 처음 상태로 되돌린다.
    fun start() {
        // 카드 종류 인덱스를 두 장씩 연속해서 만들기 위해 정수 나눗셈을 사용한다.
        // index 가 0, 1일 때는 0 / 2, 1 / 2가 모두 0이므로 [0, 0]이 된다.
        // index 가 2, 3일 때는 2 / 2, 3 / 2가 모두 1이므로 [1, 1]이 된다.
        // 이런 방식으로 index 가 두 칸씩 진행될 때마다 같은 카드 종류 번호가 두 번 반복된다.
        // 따라서 전체 결과는 [0, 0, 1, 1, 2, 2, ...] 형태가 된다.
        // 카드 종류 수의 두 배 길이 배열을 만들면 16장의 카드 구성이 완성된다.
        cardIndices = Array(cardTypeCount * 2) { index -> index / 2 }

        // cardIndices.shuffle()
        flipCount = 0
        openedCardIndex = null
    }

    fun isGameOver(): Boolean {
        return cardIndices.all { cardIndex ->
            cardIndex == null
        }
    }

    // 카드 선택을 처리하는 함수. 선택한 카드의 인덱스를 받아서 선택 결과를 반환한다.
    fun selectCard(index: Int): SelectResult {
        // 이미 짝이 맞은 카드는 다시 선택할 수 없다.
        val cardType = cardIndices[index] ?: return SelectResult.ALREADY_MATCHED

        // 이미 열려 있는 카드를 다시 누른 경우도 거부한다.
        if (openedCardIndex == index) {
            return SelectResult.ALREADY_OPEN
        }

        // 카드가 열리는 순간이니, 뒤집은 횟수를 증가시킨다.
        flipCount++

        // 아직 열려 있는 카드가 없다면 이번 카드가 첫 번째 카드가 된다.
        if (openedCardIndex == null) {
            openedCardIndex = index
            return SelectResult.FIRST_OPENED
        }

        // 열려 있는 카드의 종류. null이 될 수 없지만, null이 될 경우를 대비해서 안전하게 처리한다.
        val openedCardType = cardIndices[openedCardIndex!!] ?: return SelectResult.ALREADY_MATCHED
        if (openedCardType == cardType) {
            // 카드 종류가 일치하면, 두 카드를 모두 null로 만들어야 하는데,
            // 여기서 하지 않고 UI 갱신 후 removeCard() 에서 한다
            //cardIndices[openedCardIndex!!] = null
            //cardIndices[index] = null
            openedCardIndex = null
            return SelectResult.MATCHED
        } else {
            // 카드 종류가 일치하지 않으면, 이번에 선택한 카드가 새로 열린 카드가 된다.
            openedCardIndex = index
            return SelectResult.MISMATCHED
        }
    }

    fun removeCard(index: Int) {
        cardIndices[index] = null
    }

    // 현재 게임 상태를 저장용 JSON 문자열로 바꾼다.
    fun toJson(): String {
        val json = JSONObject()
        val cardIndicesJson = JSONArray()

        for (cardIndex in cardIndices) {
            if (cardIndex == null) {
                cardIndicesJson.put(JSONObject.NULL)
            } else {
                cardIndicesJson.put(cardIndex)
            }
        }

        json.put("cardTypeCount", cardTypeCount)
        json.put("cardIndices", cardIndicesJson)
        json.put("openedCardIndex", openedCardIndex ?: JSONObject.NULL)
        json.put("flipCount", flipCount)
        return json.toString()
    }

    // 저장해 둔 JSON 문자열에서 모델 상태를 다시 읽어온다.
    fun loadFromJson(str: String) {
        val json = JSONObject(str)
        val cardIndicesJson = json.getJSONArray("cardIndices")

        cardIndices = Array(cardIndicesJson.length()) { index ->
            if (cardIndicesJson.isNull(index)) {
                null
            } else {
                cardIndicesJson.getInt(index)
            }
        }

        openedCardIndex = if (json.isNull("openedCardIndex")) {
            null
        } else {
            json.getInt("openedCardIndex")
        }
        flipCount = json.getInt("flipCount")
    }
}
