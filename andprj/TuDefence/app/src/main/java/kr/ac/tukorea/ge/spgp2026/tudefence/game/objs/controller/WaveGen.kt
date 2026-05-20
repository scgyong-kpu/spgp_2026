package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy.Fly

class WaveGen(
    private val gctx: GameContext,
    private val world: World<MainLayer>,
) : IGameObject {
    private var time = 0f
    private var waveTime = 0f
    private var interval = INTERVAL_INIT
    private var wave = 0
    private var bossPhase = false
    private var flySpeedRatio = 1.0f

    override fun update(gctx: GameContext) {
        val slowedFrameTime = gctx.frameTime / TIME_SCALE
        waveTime += slowedFrameTime
        if (bossPhase) {
            if (waveTime > WAVE_INTERVAL || world.objectsAt(MainLayer.ENEMY).isEmpty()) {
                waveTime = 0f
                bossPhase = false
                wave++
                flySpeedRatio *= 1.0f
            }
            return
        }

        time += slowedFrameTime
        if (time > interval) {
            spawn()
            time -= interval
            interval *= INTERVAL_DECAY
            if (interval < INTERVAL_MIN) {
                interval = INTERVAL_MIN
            }
        }
        if (waveTime > WAVE_INTERVAL) {
            bossPhase = true
            spawn()
            waveTime = 0f
        }
    }

    override fun draw(canvas: Canvas) {
//        if (BuildConfig.DEBUG) {
//            canvas.drawPath(Fly.path, paint)
//        }
    }

//    private val paint by lazy {
//        Paint().apply {
//            style = Paint.Style.STROKE
//            strokeWidth = 5f
//            color = Color.MAGENTA
//        }
//    }

    private fun spawn() {
        val fly = Fly.get(gctx, bossPhase, flySpeedRatio)
        world.add(fly, MainLayer.ENEMY)
    }

    companion object {
        private const val INTERVAL_INIT = 2.0f
        private const val INTERVAL_MIN = 0.1f
        private const val INTERVAL_DECAY = 0.995f
        private const val WAVE_INTERVAL = 30.0f
        private const val TIME_SCALE = 3.0f
    }
}
