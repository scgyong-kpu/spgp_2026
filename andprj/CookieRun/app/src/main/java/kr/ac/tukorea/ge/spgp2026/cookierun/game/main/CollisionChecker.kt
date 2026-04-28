package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Canvas
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class CollisionChecker(
    private val world: World<MainScene.Layer>,
    private val player: Player,
) : IGameObject {
    override fun update(gctx: GameContext) {
        // Player 는 한 명뿐이므로 바깥쪽은 한 번만 잡고,
        // 안쪽 Item 목록만 뒤에서 앞으로 돌면서 충돌을 검사한다.
        // Item 을 즉시 remove() 하더라도 역순 순회라 아직 보지 않은 앞쪽 객체에는 영향이 적다.
        world.forEachReversedAt(MainScene.Layer.ITEM) { itemObject ->
            val item = itemObject as? JellyItem ?: return@forEachReversedAt

            if (!player.collidesWith(item)) return@forEachReversedAt

            Log.d(
                javaClass.simpleName,
                "Collision !! Player - Item(index=${item.index}, x=${item.collisionRect.left}, y=${item.collisionRect.top})",
            )
            world.remove(item, MainScene.Layer.ITEM)
        }
    }

    override fun draw(canvas: Canvas) {
    }
}
