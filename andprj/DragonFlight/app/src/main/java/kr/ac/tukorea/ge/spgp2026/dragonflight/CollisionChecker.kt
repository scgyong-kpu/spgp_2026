package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class CollisionChecker(private val gctx: GameContext) : IGameObject {
    override fun update(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return

        // 충돌 처리는 보통 "총알 목록"과 "적 목록"을 함께 보며 진행한다.
        // 첫 단계에서는 아직 Bullet 과 Enemy 에 충돌 범위를 넣지 않았으므로,
        // 두 layer 를 실제로 꺼내 와서 이중 loop 를 돌기 시작하는 데까지만 만든다.
        // 다음 commit 에서 각 객체가 collisionRect 를 제공하면,
        // 이 자리에서 Bullet 과 Enemy 가 겹치는지를 검사하게 된다.
        for (bulletObject in scene.world.objectsAt(MainScene.Layer.BULLET)) {
            val bullet = bulletObject as? Bullet ?: continue
            for (enemyObject in scene.world.objectsAt(MainScene.Layer.ENEMY)) {
                val enemy = enemyObject as? Enemy ?: continue

                // 지금 단계는 "누가 누구와 비교될지"만 잡는 commit 이다.
                // 그래서 아직 bullet / enemy 를 실제로 사용하지 않고,
                // 다음 단계에서 collisionRect, intersects, remove 로 이어질 준비만 한다.
            }
        }
    }

    override fun draw(canvas: Canvas) {
    }
}
