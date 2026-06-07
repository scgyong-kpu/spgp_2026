package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

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

    init {
        srcRect = Rect()
        setSize(SIZE, SIZE)
    }

    private fun init(x: Float, y: Float): Explosion {
        elapsedTime = 0f
        setCenter(x, y)
        updateFrame()
        return this
    }

    override fun update(gctx: GameContext) {
        elapsedTime += gctx.frameTime
        if (elapsedTime >= DURATION) {
            gctx.mainWorld().remove(this, MainLayer.EXPLOSION)
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
        fun get(gctx: GameContext, x: Float, y: Float): Explosion {
            val world = gctx.mainWorld()
            val explosion = world.obtain(Explosion::class.java) ?: Explosion(gctx)
            return explosion.init(x, y)
        }

        private const val DURATION = 0.5f
        private const val SIZE = 150f
        private const val FPS = 40f
        private const val FRAME_COUNT = 20
        private const val FRAME_SIZE = 128
    }
}
