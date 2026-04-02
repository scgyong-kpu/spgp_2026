package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Enemy(gctx: GameContext, x: Float) : Sprite(gctx, R.mipmap.enemy) {
    // Enemy 는 터치한 x 위치만 받아 화면 위에서 시작한다.
    // y 시작 위치는 "적은 위에서 내려온다"는 규칙으로 정해져 있으므로
    // 생성자에서 매번 넘기기보다 Enemy 안에서 고정하는 편이 더 읽기 쉽다.
    override var width = ENEMY_WIDTH
    override var height = ENEMY_HEIGHT
    override var x = x
    override var y = ENEMY_HEIGHT / 2f

    override fun update(gctx: GameContext) {
        // 이번 단계에서는 Enemy 가 위에서 아래로 지나가는 움직임만 먼저 확인한다.
        // 화면 밖으로 나갔을 때 삭제하는 처리는 다음 커밋에서 따로 다룬다.
        y += SPEED * gctx.frameTime
    }

    companion object {
        const val ENEMY_WIDTH = 180f
        const val ENEMY_HEIGHT = 180f
        const val SPEED = 240f
    }
}
