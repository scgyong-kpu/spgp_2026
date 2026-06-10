package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.data.Song

class PretBg(gctx: GameContext,
             private val song: Song,
             private val musicTimeProvider: () -> Float,
): Sprite(gctx, kr.ac.tukorea.ge.spgp2026.taptu.R.mipmap.bg) {
    private var nextNoteIndex = 0

    val screenWidth = gctx.metrics.width
    val screenHeight = gctx.metrics.height
    val overlayRect = RectF(0f, 0f, screenWidth, screenHeight)
    private var beatOn = 0f
    init {
        val centerX = screenWidth / 2
        val centerY = screenHeight / 2
        setCenterProportionalWidth(centerX, centerY, screenWidth)

        beatOn = song.noteAt(0)?.time ?: 0f
        while (beatOn > 0 && song.bpm > 0) {
            val oneBeat = 60f / song.bpm
            if (beatOn < oneBeat) {
                break
            }
            beatOn -= oneBeat
        }
    }

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        alpha = 0
    }
    val animator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 100
            addUpdateListener { animator ->
                val progress = animator.animatedValue as Float
                val scale = 1.0f + 0.5f * progress
                overlayRect.set(
                    screenWidth/2 - scale * screenWidth / 2, 0f,
                    screenWidth/2 + scale * screenWidth / 2, screenHeight)
                paint.alpha = (255 * (1.0f - progress)).toInt()
            }
        }
    }

    override fun update(gctx: GameContext) {
        val musicTime = musicTimeProvider()

        if (musicTime > beatOn && song.bpm > 0) {
            animator.start()
            val oneBeat = 60f / song.bpm
            beatOn += oneBeat
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawBitmap(bitmap, null, overlayRect, paint)
    }
}
