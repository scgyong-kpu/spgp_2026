package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.BuildConfig
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy.Fly
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main.MainScene
import kotlin.random.Random

class WaveGen(
    private val gctx: GameContext,
    private val world: World<MainScene.Layer>,
) : IGameObject {
    private var elapsedTime = 0f

    override fun update(gctx: GameContext) {
        elapsedTime += gctx.frameTime
        if (elapsedTime < SPAWN_INTERVAL) return

        elapsedTime -= SPAWN_INTERVAL
        spawn()
    }

    override fun draw(canvas: Canvas) {
        if (BuildConfig.DEBUG) {
            canvas.drawPath(Fly.path, paint)
        }
    }

    private val paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 5f
            color = Color.MAGENTA
        }
    }

    private fun spawn() {
        val fly = if (Random.nextInt(10) == 0) {
            Fly.boss(gctx)
        } else {
            Fly.get(gctx)
        }
        world.add(fly, MainScene.Layer.ENEMY)
    }

    companion object {
        private const val SPAWN_INTERVAL = 1.1f
    }
}
