package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Canvas
import android.graphics.Rect
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// SheetSprite 는 상태마다 서로 다른 Rect 목록을 사용하는 스프라이트 공통 클래스다.
// AnimSprite 가 "가로로 이어진 한 줄 strip" 을 fps 기준으로 자동 분할해서 그리는 쪽이라면,
// SheetSprite 는 "상태마다 프레임 Rect 집합이 따로 있는 이미지" 를 그대로 받아 그리는 쪽에 맞춘다.
// 즉 프레임 계산 방식은 다르지만, Bitmap 을 실제 Canvas 에 그리는 기반은 Sprite/AnimSprite 를 그대로 활용한다.
open class SheetSprite(
    gctx: GameContext,
    resId: Int,
    fps: Float,
) : AnimSprite(gctx, resId, fps, 1) {
    // Player 같은 하위 클래스가 현재 상태에 맞는 Rect 목록을 넣어 주면,
    // draw() 는 그 목록 중 현재 시간에 해당하는 프레임만 골라 그린다.
    protected var frameRects: List<Rect> = listOf()

    override fun draw(canvas: Canvas) {
        // 상태 프레임이 아직 준비되지 않았으면 그릴 수 있는 영역이 없으므로 그냥 빠져나간다.
        if (frameRects.isEmpty()) {
            return
        }

        // 상태 프레임 목록이 준비되어 있으면,
        // 생성 시각과 fps 를 이용해 현재 보여줄 프레임만 선택해서 그린다.
        val time = (System.currentTimeMillis() - createdOn) / 1000f
        val frameIndex = ((time * fps).toInt()) % frameRects.size
        canvas.drawBitmap(bitmap, frameRects[frameIndex], dstRect, null)
    }
}
