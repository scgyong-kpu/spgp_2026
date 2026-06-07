package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.mainWorld

class Explosion private constructor(gctx: GameContext):
    Sprite(gctx, R.mipmap.explosion), IRecyclable
{
    private var elapsedTime = 0f
    private val paint = Paint().apply {
        alpha = MAX_ALPHA
    }

    init {
        srcRect = Rect()
        setSize(SIZE, SIZE)
    }

    private fun init(x: Float, y: Float): Explosion {
        elapsedTime = 0f
        setCenter(x, y)
        updateFrame()
        updateVisualState()
        return this
    }

    override fun update(gctx: GameContext) {
        elapsedTime += gctx.frameTime
        if (elapsedTime >= DURATION) {
            gctx.mainWorld().remove(this, MainLayer.EXPLOSION)
            return
        }
        updateFrame()
        updateVisualState()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint)
    }

    private fun updateFrame() {
        // explosion.png 는 128x128 frame 20장이 한 줄로 이어진 strip 이미지이다.
        // elapsedTime 과 FPS 로 현재 frame index 를 고르고, Sprite 의 srcRect 를 그 frame 으로 맞춘다.
        val frameIndex = ((elapsedTime * FPS).toInt()).coerceIn(0, FRAME_COUNT - 1)
        srcRect?.set(
            frameIndex * FRAME_SIZE,
            0,
            (frameIndex + 1) * FRAME_SIZE,
            FRAME_SIZE,
        )
    }

    private fun updateVisualState() {
        // elapsedTime / DURATION 은 생성 직후 0.0, 사라지기 직전 1.0 에 가까운 진행률이다.
        // 이 진행률을 하나만 계산해 scale 과 alpha 에 함께 사용하면,
        // 폭발이 커지면서 동시에 살짝 투명해지는 효과를 같은 시간축에 맞출 수 있다.
        val progress = (elapsedTime / DURATION).coerceIn(0f, 1f)
        val scale = START_SCALE + (END_SCALE - START_SCALE) * progress
        val alpha = MAX_ALPHA + ((MIN_ALPHA - MAX_ALPHA) * progress).toInt()

        setSize(SIZE * scale, SIZE * scale)
        paint.alpha = alpha
    }

    override fun onRecycle() {
    }

    companion object {
        fun get(gctx: GameContext, x: Float, y: Float): Explosion {
            val world = gctx.mainWorld()
            val explosion = world.obtain(Explosion::class.java) ?: Explosion(gctx)
            return explosion.init(x, y)
        }

        private const val DURATION = 0.5f
        private const val SIZE = 150f
        private const val START_SCALE = 1.0f
        private const val END_SCALE = 2.0f
        private const val MAX_ALPHA = 255
        private const val MIN_ALPHA = 128
        private const val FPS = 40f
        private const val FRAME_COUNT = 20
        private const val FRAME_SIZE = 128
    }
}
