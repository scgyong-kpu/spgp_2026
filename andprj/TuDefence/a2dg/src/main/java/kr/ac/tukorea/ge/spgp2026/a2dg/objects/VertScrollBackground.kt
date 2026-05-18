package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 작년 DragonFlight 의 VertScrollBackground 를 Kotlin 구조로 옮긴 공통 클래스이다.
// y 를 Sprite 중심점이 아니라 "배경이 얼마나 내려왔는가"를 나타내는 스크롤 양처럼 사용하고,
// draw() 에서는 현재 스크롤 위치를 tileHeight 로 나눈 나머지를 기준으로
// 같은 bitmap 을 세로로 여러 번 반복해서 이어 그린다.
open class VertScrollBackground(
    gctx: GameContext,
    resId: Int,
    private val speed: Float,
) : Sprite(gctx, resId) {
    private val screenWidth = gctx.metrics.width
    private val screenHeight = gctx.metrics.height
    private val tileHeight = bitmapHeight * screenWidth / bitmapWidth.toFloat()

    init {
        // 한 장의 배경 이미지는 화면 가로폭에 맞춘 채 원본 비율을 유지한다.
        // 실제 draw() 는 이 크기의 배경 조각을 세로로 반복해서 붙인다.
        setCenterProportionalWidth(screenWidth / 2f, screenHeight / 2f, screenWidth)
    }

    override fun update(gctx: GameContext) {
        // y 값을 중심점이 아니라 누적 스크롤 양으로 사용하므로,
        // 배경 자체를 이동시키지 않고 "어디서부터 반복 배치를 시작할지"만 바꾼다고 보면 된다.
        y += speed * gctx.frameTime
    }

    override fun draw(canvas: Canvas) {
        var curr = y % tileHeight
        if (curr > 0f) curr -= tileHeight
        while (curr < screenHeight) {
            dstRect.set(0f, curr, screenWidth, curr + tileHeight)
            canvas.drawBitmap(bitmap, null, dstRect, null)
            curr += tileHeight
        }
    }
}
