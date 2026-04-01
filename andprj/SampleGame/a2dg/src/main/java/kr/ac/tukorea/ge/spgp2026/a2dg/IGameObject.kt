package kr.ac.tukorea.ge.spgp2026.a2dg

import android.graphics.Canvas

interface IGameObject {
    fun update(gctx: GameContext)
    fun draw(canvas: Canvas)
}
