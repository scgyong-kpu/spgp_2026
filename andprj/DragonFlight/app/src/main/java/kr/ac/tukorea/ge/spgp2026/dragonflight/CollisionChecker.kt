package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class CollisionChecker(private val gctx: GameContext) : IGameObject {
    override fun update(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return
        val bullets = scene.world.objectsAt(MainScene.Layer.BULLET)

        // 바깥쪽은 Enemy 목록을 읽기 전용으로 훑고,
        // 안쪽 Bullet 목록만 뒤에서 앞으로 돈다.
        // 이렇게 하면 "적 하나를 기준으로 어떤 총알이 닿았는지"를 보기 쉬우면서도,
        // 충돌한 Bullet 은 즉시 remove() 해도 같은 BULLET layer 순회를 계속 안전하게 진행할 수 있다.
        var collisionHandled = false
        for (enemyObject in scene.world.objectsAt(MainScene.Layer.ENEMY)) {
            val enemy = enemyObject as? Enemy ?: continue
            for (i in bullets.lastIndex downTo 0) {
                if (collisionHandled) break
                val bullet = bullets[i] as? Bullet ?: continue

                if (RectF.intersects(bullet.collisionRect, enemy.collisionRect)) {
                    Log.d(javaClass.simpleName, "Collision !! Enemy(level=${enemy.level}, x=${enemy.x}) - Bullet(y=${bullet.y})")
                    scene.world.remove(bullet, MainScene.Layer.BULLET)
                    scene.world.remove(enemy, MainScene.Layer.ENEMY)

                    // 바깥쪽 Enemy 목록은 objectsAt() 로 읽고 있으므로,
                    // 현재 Enemy 를 remove() 한 뒤 그 iterator 를 계속 진행하면
                    // 다시 ConcurrentModificationException 문제가 생길 수 있다.
                    // 그래서 이번 단계에서는 한 번 충돌을 처리했다고 표시해 두고,
                    // 현재 안쪽 루프와 바깥 for 문을 곧바로 빠져나간다.
                    collisionHandled = true
                }
            }
            if (collisionHandled) break
        }
    }

    override fun draw(canvas: Canvas) {
    }
}
