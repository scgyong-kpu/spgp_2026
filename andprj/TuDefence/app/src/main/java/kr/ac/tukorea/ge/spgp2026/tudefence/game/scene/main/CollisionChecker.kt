package kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main

import android.graphics.Canvas
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.common.collides
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy.Fly
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.weapon.Shell

class CollisionChecker(gctx: GameContext, val world: World<MainScene.Layer>): IGameObject {
    override fun update(gctx: GameContext) {
        world.forEachReversedAt(MainScene.Layer.SHELL) shellLoop@ { shellObject ->
            val shell = shellObject as? Shell ?: return@shellLoop
            var shellRemoved = false

            world.forEachReversedAt(MainScene.Layer.ENEMY) enemyLoop@ { flyObject ->
                if (shellRemoved) return@enemyLoop
                val fly = flyObject as? Fly ?: return@enemyLoop

                if (shell.collides(fly)) {
                    Log.d(javaClass.simpleName, "Collision! $shell $fly")
                    world.remove(shell, MainScene.Layer.SHELL)
                    world.remove(fly, MainScene.Layer.ENEMY)
                    shellRemoved = true
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
    }
}
