package kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main

import android.animation.ValueAnimator
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.bg.TiledBackground

class MainScene(gctx: GameContext): Scene(gctx) {
    enum class Layer {
        BG,
    }

    override val world = World(Layer.entries.toTypedArray())
    private val background = TiledBackground(gctx, "map/desert.tmj", tileWidth = 100f, tileHeight = 100f)
    private val tileSizeAnimator = ValueAnimator.ofFloat(100f, 300f).apply {
        // 임시 시각 확인용 animation 이다.
        // TiledBackground.setTileSize() 가 실제 draw 크기에 반영되는지 보기 위해 tile 크기를 계속 왕복시킨다.
        duration = 1_000L
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        addUpdateListener { animator ->
            val size = animator.animatedValue as Float
            background.setTileSize(size, size)
        }
    }

    init {
        // GameActivity 에서 기준 좌표계를 3200x1800 으로 잡았고,
        // desert.tmj 는 32x18 tile map 이므로 tile 하나를 100x100 으로 그리면 화면을 정확히 채운다.
        world.add(background, Layer.BG)
        tileSizeAnimator.start()
    }

    override fun onExit() {
        tileSizeAnimator.cancel()
    }
}
