package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.data.Note
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.mainWorld


class CircleNoteSprite private constructor(
    gctx: GameContext,
    private val musicTimeProvider: () -> Float,
): INoteSprite, IRecyclable {
    override lateinit var note: Note

    override var x = 0f
    override var y = 0f
    val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.BLUE
    }
    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x7FBF0000
    }

    fun init(note: Note) {
        this.note = note
        x = FallingNoteSprite.xFromPret(note.pret)
        y = 300 + (note.time % 10.0f) * 100f
    }
    override fun update(gctx: GameContext) {
        val musicTime = musicTimeProvider()
        val timeDiff = musicTime - note.time
        if (timeDiff > 1.0) {
            gctx.mainWorld().remove(this, MainLayer.NOTE)
            return
        }
    }

    override fun draw(canvas: Canvas) {
        val musicTime = musicTimeProvider()
        val timeDiff = note.time - musicTime
        linePaint.alpha = if (timeDiff < 0.5f) { 255
        } else if (timeDiff > 2f) { 31
        } else {
            // 0.5초에서 2.0초 사이에 255에서 31로 점진적으로 감소
            (255 - ((timeDiff - 0.5f) / 1.5f) * (255 - 31)).toInt()
        }
        val radius = (timeDiff + 1.0f) * CIRCLE_RADIUS
        canvas.drawCircle(x, y, radius, linePaint)
        canvas.drawCircle(x, y, CIRCLE_RADIUS, circlePaint)
    }

    override fun onRecycle() {
    }

    companion object {
        const val CIRCLE_RADIUS = 50f

        fun get(gctx: GameContext, note: Note, musicTimeProvider: () -> Float): INoteSprite {
            val cns = gctx.mainWorld().obtain(CircleNoteSprite::class.java) ?: CircleNoteSprite(gctx, musicTimeProvider)
            cns.init(note)
            return cns
        }
    }
}