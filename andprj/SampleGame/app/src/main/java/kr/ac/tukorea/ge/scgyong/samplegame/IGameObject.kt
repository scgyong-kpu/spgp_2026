package kr.ac.tukorea.ge.scgyong.samplegame

interface IGameObject {
    fun update(gctx: GameContext)
    fun draw(canvas: android.graphics.Canvas)
}