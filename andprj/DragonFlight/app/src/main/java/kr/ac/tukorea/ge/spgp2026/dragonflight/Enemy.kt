package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Enemy(gctx: GameContext, x: Float) : AnimSprite(gctx, R.mipmap.enemy_01, FPS) {
    // Enemy 는 터치한 x 위치만 받아 화면 위에서 시작한다.
    // y 시작 위치는 "적은 위에서 내려온다"는 규칙으로 정해져 있으므로
    // 생성자에서 매번 넘기기보다 Enemy 안에서 고정하는 편이 더 읽기 쉽다.
    //
    // x, y 는 Sprite 의 중심점이다.
    // 그래서 y 를 ENEMY_HEIGHT / 2f 로 두면 화면 위에 "딱 붙어" 시작하고,
    // y 를 -ENEMY_HEIGHT / 2f 로 두면 이미지 전체가 화면 밖에 있는 상태에서 내려오게 된다.
    // DragonFlight 류 게임에서는 적이 화면 위 바깥에서 진입하는 느낌이 더 자연스러우므로
    // 여기서는 후자를 사용한다.
    override var width = ENEMY_WIDTH
    override var height = ENEMY_HEIGHT
    override var x = x
    override var y = -ENEMY_HEIGHT / 2f

    override fun update(gctx: GameContext) {
        // 아래쪽으로 움직일 때도 중심점 y 를 더한다.
        // 삭제 조건은
        //   y - height / 2f > gctx.metrics.height
        // 처럼 "적의 윗변이 화면 아래를 지나갔는지"를 보면
        // 적이 완전히 안 보인 뒤에 제거할 수 있다.
        y += SPEED * gctx.frameTime

        if (y - height / 2f > gctx.metrics.height) {
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.ENEMY)
        }
    }

    companion object {
        const val ENEMY_WIDTH = 180f
        const val ENEMY_HEIGHT = 180f
        const val SPEED = 240f
        const val FPS = 10f
    }
}
