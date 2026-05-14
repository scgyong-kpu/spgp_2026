package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.weapon

import android.graphics.Canvas
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main.MainScene
import kotlin.math.cos
import kotlin.math.sin

class Shell private constructor(gctx: GameContext):
    Sprite(gctx, R.mipmap.shell), IRecyclable
{
    private var dx = 0f
    private var dy = 0f
    private var angle = 0f

    init {
        setSize(SIZE, SIZE)
    }

    private fun init(x: Float, y: Float, angle: Float): Shell {
        setCenter(x, y)
        this.angle = angle
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
        fun get(gctx: GameContext, x: Float, y: Float, angle: Float): Shell {
            val world = (gctx.scene as MainScene).world
            val shell = world.obtain(Shell::class.java) ?: Shell(gctx)
            return shell.init(x, y, angle)
        }

        private const val SIZE = 20f
        private const val SPEED = 600f
    }
}
