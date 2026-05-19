package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.effect

import android.graphics.Rect
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main.MainScene

class Explosion private constructor(gctx: GameContext):
    Sprite(gctx, R.mipmap.explosion), IRecyclable
{
    private var elapsedTime = 0f

    init {
        srcRect = Rect()
    }

    private fun init(x: Float, y: Float, radius: Float): Explosion {
        elapsedTime = 0f
        setSize(radius * 2f, radius * 2f)
        setCenter(x, y)
        updateFrame()
        return this
    }

    override fun update(gctx: GameContext) {
        elapsedTime += gctx.frameTime
        if (elapsedTime >= DURATION) {
            (gctx.scene as MainScene).world.remove(this, MainScene.Layer.EXPLOSION)
            return
        }
        updateFrame()
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

    override fun onRecycle() {
    }

    companion object {
        fun get(gctx: GameContext, x: Float, y: Float, radius: Float): Explosion {
            val world = (gctx.scene as MainScene).world
            val explosion = world.obtain(Explosion::class.java) ?: Explosion(gctx)
            return explosion.init(x, y, radius)
        }

        private const val DURATION = 1.0f
        private const val FPS = 20f
        private const val FRAME_COUNT = 20
        private const val FRAME_SIZE = 128
    }
}
