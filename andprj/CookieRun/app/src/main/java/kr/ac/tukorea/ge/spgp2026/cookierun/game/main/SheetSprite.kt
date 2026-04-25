package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Canvas
import android.graphics.Rect
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// SheetSprite 는 상태별로 서로 다른 Rect 목록을 가지고 있는 스프라이트용 공통 클래스다.
// AnimSprite 가 "가로로 이어진 한 줄 strip" 을 다루는 쪽이라면,
// SheetSprite 는 "상태마다 프레임 Rect 집합이 따로 있는 이미지" 를 다루는 데 맞춰 둔다.
open class SheetSprite(
    gctx: GameContext,
    resId: Int,
    private val fps: Float,
) : Sprite(gctx, resId) {
    protected var frameRects: List<Rect> = listOf()
        private set

    protected val createdOn = System.currentTimeMillis()

    protected fun setFrameRects(rects: List<Rect>) {
        frameRects = rects
    }

    override fun draw(canvas: Canvas) {
        syncDstRect()

        if (frameRects.isEmpty()) {
            return
        }

        // 상태별 프레임 목록이 준비되어 있으면,
        // 생성 시각과 fps 를 이용해 현재 보여줄 프레임만 선택해서 그린다.
        val time = (System.currentTimeMillis() - createdOn) / 1000f
        val frameIndex = ((time * fps).toInt()) % frameRects.size
        canvas.drawBitmap(bitmap, frameRects[frameIndex], dstRect, null)
    }
}
