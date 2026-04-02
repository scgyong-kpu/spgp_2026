package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Bullet(
    gctx: GameContext,
    startX: Float,
    startY: Float,
) : Sprite(gctx, R.mipmap.laser_1) {
    override var width = BULLET_WIDTH
    override var height = BULLET_HEIGHT
    override var x = startX
    override var y = startY

    // 지금 단계의 충돌 범위는 draw 에 쓰는 목적 사각형과 같은 값으로 본다.
    // 즉 Bullet 은 별도 collision box 를 따로 계산하지 않고,
    // 현재 화면에 그려질 영역(dstRect)을 그대로 collisionRect 로 사용한다.
    val collisionRect: RectF
        get() {
            syncDstRect()
            return dstRect
        }

    override fun update(gctx: GameContext) {
        // 현재 Bullet 은 x 는 그대로 두고 y 만 감소시키며 위쪽으로 직진한다.
        y -= SPEED * gctx.frameTime

        // 총알이 화면 위를 완전히 벗어나면 현재 Scene 의 BULLET layer 에서 제거한다.
        if (y + height / 2f < 0f) {
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.BULLET)
        }
    }

    companion object {
        const val BULLET_WIDTH = 68f
        const val BULLET_HEIGHT = BULLET_WIDTH * 40f / 28f
        const val SPEED = 2000f
    }
}
