package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class CollisionChecker(private val gctx: GameContext) : IGameObject {
    override fun update(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return

        // 충돌 처리는 보통 "총알 목록"과 "적 목록"을 함께 보며 진행한다.
        // 첫 단계에서는 아직 Bullet 과 Enemy 에 충돌 범위를 넣지 않았으므로,
        // 두 layer 를 실제로 꺼내 와서 이중 loop 를 돌기 시작하는 데까지만 만든다.
        // 이제는 Bullet / Enemy 가 collisionRect 를 제공하므로,
        // 두 사각형이 겹치면 충돌이 발생했다고 보고 로그만 찍는다.
        for (bulletObject in scene.world.objectsAt(MainScene.Layer.BULLET)) {
            val bullet = bulletObject as? Bullet ?: continue
            for (enemyObject in scene.world.objectsAt(MainScene.Layer.ENEMY)) {
                val enemy = enemyObject as? Enemy ?: continue

                if (RectF.intersects(bullet.collisionRect, enemy.collisionRect)) {
                    Log.d(javaClass.simpleName, "Collision !! Enemy(level=${enemy.level}, x=${enemy.x}) - Bullet(y=${bullet.y})")
                    // 아래 코드로 삭제하려 시도하면 ConcurrentModificationError 가 발생한다.
                    // scene.world.remove(bullet, MainScene.Layer.BULLET)
                    // scene.world.remove(enemy, MainScene.Layer.ENEMY)
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
    }
}
