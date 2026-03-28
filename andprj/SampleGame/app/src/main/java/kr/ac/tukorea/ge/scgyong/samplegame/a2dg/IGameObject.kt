package kr.ac.tukorea.ge.scgyong.samplegame.a2dg

import android.graphics.Canvas

interface IGameObject {
    fun update(gctx: GameContext)
    fun draw(canvas: Canvas)
}