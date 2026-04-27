package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Rect
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

class JellyItem(gctx: GameContext, index: Int): MapObject(gctx, R.mipmap.jelly, 0f, 0f, DST_SIZE, DST_SIZE) {
    // JellyItem 은 아이템 레이어에만 놓인다.
    // layer 를 계산 프로퍼티로 두면, JellyItem 마다 레이어 필드를 따로 저장하지 않아도 된다.
    override val layer get() = MainScene.Layer.ITEM

    // JellyItem 은 MapObject 아래에서 동작하는 수집 아이템이다.
    // index 는 jelly 스프라이트 시트에서 어느 칸을 보여줄지 고르는 번호다.
    // setter 에서 index 를 (행, 열)로 바꾼 다음, 그 칸의 srcRect 를 직접 계산한다.
    var index = index
        set(value) {
            if (value !in 0..<JELLY_COUNT) {
                Log.e(javaClass.simpleName, "Invalid index: $value")
                return
            }
            field = value
            val x = value % ITEMS_IN_A_ROW
            val y = value / ITEMS_IN_A_ROW
            val left = x * (SRC_SIZE + SRC_BORDER) + SRC_BORDER
            val top = y * (SRC_SIZE + SRC_BORDER) + SRC_BORDER
            srcRect?.set(left, top, left + SRC_SIZE, top + SRC_SIZE)
        }

    init {
        // index setter 에서 srcRect 를 계산하므로, 생성 시에도 setter 를 한 번 거쳐야 한다.
        // 그렇지 않으면 Rect() 가 비어 있는 채로 남아 drawBitmap() 이 아무 것도 그리지 않는다.
        srcRect = Rect()
        this.index = index
    }

    companion object {
        // jelly.png 는 한 칸의 실제 이미지 크기가 66px 이고, 칸 사이에 2px 테두리(border)가 있다.
        // 그래서 index 하나가 곧바로 "몇 번째 칸인지"를 뜻하도록, srcRect 계산식에 66 과 2를 함께 쓴다.
        const val SRC_SIZE = 66
        const val SRC_BORDER = 2
        const val JELLY_COUNT = 60
        const val ITEMS_IN_A_ROW = 30

        const val DST_SIZE = 100f
    }
}
