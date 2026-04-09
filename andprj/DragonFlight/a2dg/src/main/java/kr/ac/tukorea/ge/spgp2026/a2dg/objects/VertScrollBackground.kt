package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 작년 DragonFlight 의 VertScrollBackground 를 Kotlin 구조로 옮기되,
// 지금 단계에서는 "세로로 반복 배치"만 먼저 넣는다.
// 시간에 따라 자동으로 스크롤되는 기능은 다음 단계로 미루고,
// draw() 에서 같은 bitmap 을 세로로 여러 번 반복해서 이어 그리는 부분만 공통화한다.
open class VertScrollBackground(
    gctx: GameContext,
    resId: Int,
) : Sprite(gctx, resId) {
    private val screenWidth = gctx.metrics.width
    private val screenHeight = gctx.metrics.height
    private val tileHeight = bitmapHeight * screenWidth / bitmapWidth.toFloat()

    init {
        // 한 장의 배경 이미지는 화면 가로폭에 맞춘 채 원본 비율을 유지한다.
        // 실제 draw() 는 이 크기의 배경 조각을 세로로 반복해서 붙인다.
        setCenterProportionalWidth(screenWidth / 2f, screenHeight / 2f, screenWidth)
    }

    override fun draw(canvas: Canvas) {
        var curr = 0f
        while (curr < screenHeight) {
            dstRect.set(0f, curr, screenWidth, curr + tileHeight)
            canvas.drawBitmap(bitmap, null, dstRect, null)
            curr += tileHeight
        }
    }
}
