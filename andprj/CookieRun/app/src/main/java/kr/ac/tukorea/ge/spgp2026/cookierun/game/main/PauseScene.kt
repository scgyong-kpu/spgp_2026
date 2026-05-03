package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Button
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

class PauseScene(gctx: GameContext) : Scene(gctx) {
    enum class Layer {
        BG, TITLE, TOUCH
    }

    override val clipsRect = true
    override val isTransparent = true

    override val world = World(Layer.entries.toTypedArray()).apply {
        // PauseScene 은 transparent Scene 이므로 아래 MainScene 도 함께 그려진다.
        // 여기서는 반투명 검정 Sprite 만 올려 아래 장면을 어둡게 보이게 한다.
        add(Sprite(gctx, R.mipmap.trans_50b).apply {
            setCenter(gctx.metrics.width / 2f, gctx.metrics.height / 2f)
            setSize(gctx.metrics.width, gctx.metrics.height)
        }, Layer.BG)

        add(Sprite(gctx, R.mipmap.cookie_run_title).apply {
            setCenter(gctx.metrics.width / 2f, 360f)
            setSize(369f, 136f)
        }, Layer.TITLE)

        // Resume 버튼은 현재 PauseScene 만 pop 해서 아래 MainScene 으로 돌아간다.
        add(Button(gctx, R.mipmap.btn_resume_n, 1450f, 100f, 200f, 75f) { pressed ->
            if (pressed) {
                pop()
            }
            false
        }, Layer.TOUCH)
    }

    override fun touchObjects(): List<IGameObject> {
        return world.objectsAt(Layer.TOUCH)
    }

    override fun onBackPressed(): Boolean {
        pop()
        return true
    }
}
