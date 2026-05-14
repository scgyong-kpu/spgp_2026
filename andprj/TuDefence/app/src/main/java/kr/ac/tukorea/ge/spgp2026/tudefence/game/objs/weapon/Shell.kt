package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.weapon

import android.graphics.Canvas
import android.graphics.Rect
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main.MainScene
import kotlin.math.cos
import kotlin.math.sin

class Shell private constructor(gctx: GameContext):
    Sprite(gctx, R.mipmap.shells), IRecyclable
{
    private var dx = 0f
    private var dy = 0f
    private var angle = 0f

    init {
        setSize(SIZE, SIZE)
    }

    private fun init(x: Float, y: Float, angle: Float, level: Int): Shell {
        setCenter(x, y)
        this.angle = angle
        val shellLevel = level.coerceIn(MIN_LEVEL, MAX_LEVEL)
        val rect = shellRects[shellLevel - 1]
        val size = rect.width().toFloat() * SRC_TO_DST_RATIO
        setSize(size, size)
        srcRect = rect
        val radians = Math.toRadians(angle.toDouble())
        dx = cos(radians).toFloat() * SPEED
        dy = sin(radians).toFloat() * SPEED
        return this
    }

    override fun update(gctx: GameContext) {
        setCenter(x + dx * gctx.frameTime, y + dy * gctx.frameTime)
        if (isOutOfWorld()) {
            (gctx.scene as MainScene).world.remove(this, MainScene.Layer.SHELL)
        }
    }

    private fun isOutOfWorld(): Boolean {
        return x < -SIZE || x > MainScene.MAP_WIDTH + SIZE ||
            y < -SIZE || y > MainScene.MAP_HEIGHT + SIZE
    }

    override fun draw(canvas: Canvas) {
        canvas.withRotation(angle, x, y) {
            super.draw(canvas)
        }
    }

    override fun onRecycle() {
    }

    companion object {
        fun get(gctx: GameContext, x: Float, y: Float, angle: Float, level: Int): Shell {
            val world = (gctx.scene as MainScene).world
            val shell = world.obtain(Shell::class.java) ?: Shell(gctx)
            return shell.init(x, y, angle, level)
        }

        private val shellRects = arrayOf(
            Rect(20, 44, 61, 85),    // level 1
            Rect(77, 36, 135, 94),   // level 2
            Rect(150, 30, 220, 100), // level 3
            Rect(233, 28, 307, 102), // level 4
            Rect(322, 23, 406, 107), // level 5
            Rect(420, 22, 506, 108), // level 6
            Rect(520, 18, 614, 112), // level 7
            Rect(626, 19, 716, 109), // level 8
            Rect(723, 10, 831, 118), // level 9
            Rect(833, 4, 952, 123),  // level 10
        )

        private const val MIN_LEVEL = 1
        private const val MAX_LEVEL = 10
        private const val SIZE = 20f
        private const val SPEED = 600f
        private const val SRC_TO_DST_RATIO = 0.48f
    }
}
