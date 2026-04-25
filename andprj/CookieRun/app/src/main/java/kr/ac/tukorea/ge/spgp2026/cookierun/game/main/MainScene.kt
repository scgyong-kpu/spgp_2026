package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.HorzScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

class MainScene(gctx: GameContext) : Scene(gctx) {
    override val clipsRect = true
    override val world = World(arrayOf(0)).apply {
        add(HorzScrollBackground(gctx, R.mipmap.cookie_run_bg_1, -50f), 0)
        add(HorzScrollBackground(gctx, R.mipmap.cookie_run_bg_2, -100f), 0)
        add(HorzScrollBackground(gctx, R.mipmap.cookie_run_bg_3, -150f), 0)
        add(Player(gctx), 0)
    }
}
