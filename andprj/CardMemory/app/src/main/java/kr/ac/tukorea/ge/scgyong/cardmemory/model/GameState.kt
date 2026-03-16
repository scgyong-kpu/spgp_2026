package kr.ac.tukorea.ge.scgyong.cardmemory.model

import org.json.JSONArray
import org.json.JSONObject

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
