package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

interface IGameObject {
    fun update(gctx: GameContext)
    fun draw(canvas: Canvas)
}
