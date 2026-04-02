package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class CollisionChecker(private val gctx: GameContext) : IGameObject {
    private val collisionPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    override fun update(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return

        // 바깥쪽 Enemy 와 안쪽 Bullet 을 모두 forEachReversedAt() 로 뒤에서 앞으로 돈다.
        // 그러면 충돌한 Bullet 이나 Enemy 를 즉시 remove() 해도
        // 각 layer 안의 아직 방문하지 않은 앞쪽 객체들을 계속 안전하게 볼 수 있다.
        // 그리고 forEachReversedAt() 는 지금 inline 함수이므로,
        // helper 호출 형태로 써도 별도 함수/람다 객체가 추가로 생기지 않고
        // 호출 위치에 그대로 펴진다고 생각하면 된다.
        scene.world.forEachReversedAt(MainScene.Layer.ENEMY) { enemyObject ->
            val enemy = enemyObject as? Enemy ?: return@forEachReversedAt
            scene.world.forEachReversedAt(MainScene.Layer.BULLET) { bulletObject ->
                val bullet = bulletObject as? Bullet ?: return@forEachReversedAt

                if (RectF.intersects(bullet.collisionRect, enemy.collisionRect)) {
                    Log.d(javaClass.simpleName, "Collision !! Enemy(level=${enemy.level}, x=${enemy.x}) - Bullet(y=${bullet.y})")
                    scene.world.remove(bullet, MainScene.Layer.BULLET)
                    scene.world.remove(enemy, MainScene.Layer.ENEMY)
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
        val scene = gctx.scene as? MainScene ?: return

        // collisionRect 가 실제 그림과 얼마나 비슷한지 눈으로 확인하기 위한 임시 디버그 표시이다.
        // Bullet, Enemy 의 충돌 범위를 빨간 사각형으로 그려 두면,
        // 나중에 "dstRect 와 collisionRect 를 다르게 잡아야 하나?"를 판단하기 쉬워진다.
        for (enemyObject in scene.world.objectsAt(MainScene.Layer.ENEMY)) {
            val enemy = enemyObject as? Enemy ?: continue
            canvas.drawRect(enemy.collisionRect, collisionPaint)
        }
        for (bulletObject in scene.world.objectsAt(MainScene.Layer.BULLET)) {
            // val bullet = bulletObject as? Bullet ?: continue
            // 위처럼 safe cast + continue 로 써도 되지만,
            // 여기서는 Kotlin 의 smart cast 예를 보여주기 위해 is 검사 형태로 적어 둔다.
            // 실전에서는 둘 중 팀이 더 읽기 좋다고 느끼는 쪽을 고르면 된다.
            if (bulletObject !is Bullet) continue
            canvas.drawRect(bulletObject.collisionRect, collisionPaint)
        }
    }
}
