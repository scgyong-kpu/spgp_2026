package kr.ac.tukorea.ge.scgyong.cardmemory.model

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
}
