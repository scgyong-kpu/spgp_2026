package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// DrawableSprite 는 Android Drawable 을 GameObject 처럼 World 에 넣어 그리기 위한 작은 adapter 이다.
// bitmap 리소스가 꼭 필요하지 않은 단색/그라데이션/shape overlay 에 사용할 수 있다.
//
// 지금은 Sprite 를 상속하지 않고 독립 IGameObject 로 둔다.
// Sprite 의 bitmap 기반 그리기와 Drawable 의 draw(bounds) 방식이 다르기 때문이다.
// 나중에 Sprite 계열 객체가 내부에 DrawableSprite 를 has-a 로 들고,
// 배경이나 강조 효과 그리기만 위임하는 구조로 확장할 수도 있다.
class DrawableSprite(
    private val drawable: Drawable,
) : IGameObject {
    private val dstRect = RectF()
    private var x = 0f
    private var y = 0f
    private var width = 0f
    private var height = 0f

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        drawable.setBounds(
            dstRect.left.toInt(),
            dstRect.top.toInt(),
            dstRect.right.toInt(),
            dstRect.bottom.toInt(),
        )
        drawable.draw(canvas)
    }

    fun setCenter(centerX: Float, centerY: Float) {
        x = centerX
        y = centerY
        syncDstRect()
    }

    fun setSize(width: Float, height: Float) {
        this.width = width
        this.height = height
        syncDstRect()
    }

    private fun syncDstRect() {
        dstRect.set(
            x - width / 2f,
            y - height / 2f,
            x + width / 2f,
            y + height / 2f,
        )
    }
}
