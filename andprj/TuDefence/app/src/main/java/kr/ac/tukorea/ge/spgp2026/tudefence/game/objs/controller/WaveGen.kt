package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.Balance
import kr.ac.tukorea.ge.spgp2026.tudefence.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy.Fly

class WaveGen(
    private val gctx: GameContext,
    private val world: World<MainLayer>,
) : IGameObject {
    private var time = 0f
    private var waveTime = 0f
    private var interval = Balance.Wave.intervalInit
    private var wave = 0
    private var bossPhase = false
    private var flySpeedRatio = 1.0f

    override fun update(gctx: GameContext) {
        val slowedFrameTime = gctx.frameTime / Balance.Wave.timeScale
        waveTime += slowedFrameTime
        if (bossPhase) {
            if (waveTime > Balance.Wave.waveInterval || world.objectsAt(MainLayer.ENEMY).isEmpty()) {
                waveTime = 0f
                bossPhase = false
                wave++
                flySpeedRatio *= Balance.Wave.speedRatioPerWave
            }
            return
        }

        time += slowedFrameTime
        if (time > interval) {
            spawn()
            time -= interval
            interval *= Balance.Wave.intervalDecay
            if (interval < Balance.Wave.intervalMin) {
                interval = Balance.Wave.intervalMin
            }
        }
        if (waveTime > Balance.Wave.waveInterval) {
            bossPhase = true
            spawn()
            waveTime = 0f
        }
    }

    override fun draw(canvas: Canvas) {
    }

    private fun spawn() {
        val fly = Fly.get(gctx, bossPhase, flySpeedRatio)
        world.add(fly, MainLayer.ENEMY)
    }
}
