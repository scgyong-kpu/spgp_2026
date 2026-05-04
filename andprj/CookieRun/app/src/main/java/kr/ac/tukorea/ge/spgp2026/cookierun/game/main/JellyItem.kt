package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

class JellyItem private constructor(
    gctx: GameContext,
): MapObject(gctx, R.mipmap.jelly) {
    // JellyItem 은 아이템 레이어에만 놓인다.
    // layer 를 계산 프로퍼티로 두면, JellyItem 마다 레이어 필드를 따로 저장하지 않아도 된다.
    override val layer get() = MainScene.Layer.ITEM

    // JellyItem 은 MapObject 아래에서 동작하는 수집 아이템이다.
    // index 는 jelly 스프라이트 시트에서 어느 칸을 보여줄지 고르는 번호다.
    // setter 에서 index 를 (행, 열)로 바꾼 다음, 그 칸의 srcRect 를 직접 계산한다.
    // collisionRect 는 dstRect 보다 약간 작게 잡아 플레이어와 부딪히는 느낌을 줄인다.
    override val collisionRect = RectF()
    var index = 0
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
    val soundResId: Int
        get() = SOUND_IDS[index % SOUND_IDS.size]

    init {
        // index setter 에서 srcRect 를 계산하므로, 생성 시에도 setter 를 한 번 거쳐야 한다.
        // 그렇지 않으면 Rect() 가 비어 있는 채로 남아 drawBitmap() 이 아무 것도 그리지 않는다.
        srcRect = Rect()

        Log.d(javaClass.simpleName, "Created: $this")
    }

    // 생성 후 위치와 index를 초기화하도록 한다. 이 함수는 재활용 된 뒤에도 불릴 예정이다.
    fun init(index: Int, left: Float, top: Float) {
        this.index = index
        dstRect.set(left, top, left + DST_SIZE, top + DST_SIZE)
        updateCollisionRect()
    }

    override fun update(gctx: GameContext) {
        super.update(gctx)
        updateCollisionRect()
    }

    override fun toString(): String {
        return "Item(${this.index},@${System.identityHashCode(this)})"
    }
    companion object {
        // jelly.png 는 한 칸의 실제 이미지 크기가 66px 이고, 칸 사이에 2px 테두리(border)가 있다.
        // 그래서 index 하나가 곧바로 "몇 번째 칸인지"를 뜻하도록, srcRect 계산식에 66 과 2를 함께 쓴다.
        const val SRC_SIZE = 66
        const val SRC_BORDER = 2
        // JellyItem 의 충돌 박스는 사방 20f 만큼 안쪽으로 줄인다.
        const val COLLISION_INSET = 20f
        const val JELLY_COUNT = 60
        const val ITEMS_IN_A_ROW = 30
        // Magnification 효과를 테스트하기 위해 먼저 "특수 젤리" index 만 이름 붙여 둔다.
        // 실제 확대 처리는 CollisionChecker 와 Player 쪽 단계에서 이어 붙일 예정이다.
        const val MAGNIFICATION_INDEX = 26

        const val DST_SIZE = 100f
        private val SOUND_IDS = intArrayOf(
            R.raw.jelly,
            R.raw.jelly_alphabet,
            R.raw.jelly_item,
            R.raw.jelly_gold,
            R.raw.jelly_coin,
            R.raw.jelly_big_coin,
        )

        fun get(gctx: GameContext, index: Int, left: Float, top: Float): JellyItem {
            val world = (gctx.scene as MainScene).world
            // World 에서 재활용 가능한 JellyItem 이 있는지 찾아본다.
            val item = world.obtain(JellyItem::class.java) ?: JellyItem(gctx)
            item.init(index, left, top)
            return item
        }
    }

    private fun updateCollisionRect() {
        collisionRect.set(dstRect)
        collisionRect.inset(COLLISION_INSET, COLLISION_INSET)
    }
}
