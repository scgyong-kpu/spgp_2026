package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Enemy(gctx: GameContext, x: Float, y: Float) : Sprite(gctx, R.mipmap.enemy) {
    // 이번 단계에서는 Enemy 가 실제로 움직이기 전,
    // 화면에 정상적으로 생성되고 배치되는지만 먼저 확인한다.
    // 그래서 생성자에서 받은 중심 좌표를 그대로 쓰고, update() 는 아직 비워 둔다.
    override var width = ENEMY_WIDTH
    override var height = ENEMY_HEIGHT
    override var x = x
    override var y = y

    companion object {
        const val ENEMY_WIDTH = 144f
        const val ENEMY_HEIGHT = 160f
    }
}
