package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Canvas
import android.graphics.Rect
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

open class AnimSprite(
    private val gctx: GameContext,
    resId: Int,
    var fps: Float,
    frameCount: Int = 0,
) : Sprite(gctx, resId) {
    protected var frameCount = 0
        set(value) {
            val imageWidth = bitmap.width
            val imageHeight = bitmap.height

            if (value == 0) {
                // frameCount 를 주지 않으면 strip 높이를 한 frame 크기로 보고,
                // 그 높이만큼의 정사각 frame 이 가로로 이어져 있다고 해석한다.
                frameWidth = imageHeight
                frameHeight = imageHeight
                field = imageWidth / imageHeight
            } else {
                frameWidth = imageWidth / value
                frameHeight = imageHeight
                field = value
            }
        }
    protected var frameWidth = 0
    protected var frameHeight = 0
    protected val createdOn = System.currentTimeMillis()

    init {
        // AnimSprite 는 strip 이미지를 반복 재생하므로 srcRect 가 항상 필요하다.
        srcRect = Rect()
        this.frameCount = frameCount
    }

    override fun draw(canvas: Canvas) {
        syncDstRect()

        // AnimSprite 는 "몇 초가 지났는가"만 알면 현재 frame 을 계산할 수 있으므로
        // 별도 time 누적 없이 생성 시각과 현재 시각 차이로 index 를 구한다.
        val time = (System.currentTimeMillis() - createdOn) / 1000f
        val frameIndex = ((time * fps).toInt()) % frameCount
        srcRect?.set(
            frameIndex * frameWidth,
            0,
            (frameIndex + 1) * frameWidth,
            frameHeight,
        )
        canvas.drawBitmap(bitmap, srcRect, dstRect, null)
    }
}
