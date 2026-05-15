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
        val shells = world.objectsAt(MainScene.Layer.SHELL)
        val flies = world.objectsAt(MainScene.Layer.ENEMY)

        // 충돌 검사는 Shell 을 기준으로 진행한다.
        // Shell 하나가 Fly 하나와 충돌하면 그 Shell 은 사라지므로,
        // 같은 Shell 로 다른 Fly 를 계속 검사할 필요가 없다.
        //
        // forEachReversedAt { ... } 형태는 삭제하면서 끝까지 순회하기에는 좋지만,
        // 람다 안에서 일반 loop 의 break 를 직접 쓸 수 없다.
        // 여기서는 "현재 Shell 의 Fly 탐색만 중단하고 다음 Shell 로 넘어가기"가 필요하므로,
        // objectsAt() 으로 목록을 얻은 뒤 index 기반 reverse loop 를 직접 사용한다.
        //
        // reversed() 나 asReversed() 는 별도 객체/view 를 만들 수 있으므로,
        // 매 프레임 실행되는 충돌 검사에서는 lastIndex downTo 0 형태가 가장 단순하고 안전하다.
        for (si in shells.lastIndex downTo 0) {
            val shell = shells[si] as? Shell ?: continue

            for (fi in flies.lastIndex downTo 0) {
                val fly = flies[fi] as? Fly ?: continue
                if (shell.collides(fly)) {
                    Log.d(javaClass.simpleName, "Collision! $shell $fly")
                    world.remove(shell, MainScene.Layer.SHELL)
                    world.remove(fly, MainScene.Layer.ENEMY)
                    // 이 break 는 안쪽 Fly loop 만 끝낸다.
                    // 바깥 Shell loop 는 계속 진행하므로, 한 프레임 안에서도 다음 Shell 의 충돌은 계속 검사한다.
                    break
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
    }
}
