package kr.ac.tukorea.ge.scgyong.cardmemory.model

class GameState(val cardTypeCount: Int) {
    var cardIndices: Array<Int?> = Array(cardTypeCount * 2) { index -> index / 2 }
    var openedCardIndex: Int? = null
    var flipCount: Int = 0
}
