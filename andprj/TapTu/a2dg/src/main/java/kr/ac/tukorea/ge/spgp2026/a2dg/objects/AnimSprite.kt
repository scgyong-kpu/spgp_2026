package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Canvas
import android.graphics.Rect
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

open class AnimSprite(
    protected val gctx: GameContext,
    resId: Int,
    var fps: Float,
    frameCount: Int = 0,
) : Sprite(gctx, resId) {
    protected var frameCount = 0
        set(value) {
            val imageWidth = bitmap.width
            val imageHeight = bitmap.height

            if (value == 0) {
                // frameCount 를 주지 않으면 strip 의 높이를 한 프레임 크기로 보고,
                // 그 높이만큼의 정사각형 프레임이 가로로 이어진 이미지라고 해석한다.
                // 예를 들어 720x200 이미지라면 200x200 프레임 3개로 보게 된다.
                frameWidth = imageHeight
                frameHeight = imageHeight
                field = imageWidth / imageHeight
            } else {
                // frameCount 를 명시하면 이미지 전체 너비를 그 수만큼 나누어 프레임 폭을 구한다.
                // 예를 들어 720x200 이미지를 frameCount=4 로 주면 180x200 프레임 4개가 된다.
                frameWidth = imageWidth / value
                frameHeight = imageHeight
                field = value
            }
        }
    protected var frameWidth = 0
    protected var frameHeight = 0
    protected val createdOn = System.currentTimeMillis()

    init {
        // AnimSprite 는 strip 이미지에서 현재 프레임 영역을 잘라 그려야 하므로 srcRect 가 꼭 필요하다.
        srcRect = Rect()
        this.frameCount = frameCount
    }

    override fun draw(canvas: Canvas) {
        // "몇 초가 지났는가"만 알면 현재 프레임 번호를 계산할 수 있으므로
        // 별도 누적 time 변수 없이 생성 시각과 현재 시각 차이로 frame index 를 구한다.
        val time = (System.currentTimeMillis() - createdOn) / 1000f
        val frameIndex = ((time * fps).toInt()) % frameCount

        // 이번 프레임에 해당하는 strip 내부 영역만 잘라 srcRect 로 지정한다.
        srcRect?.set(
            frameIndex * frameWidth,
            0,
            (frameIndex + 1) * frameWidth,
            frameHeight,
        )
        canvas.drawBitmap(bitmap, srcRect, dstRect, null)
    }
}
