package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class EnemyGenerator(
    private val gctx: GameContext,
) : IGameObject {
    private var enemyTime = 0f

    override fun update(gctx: GameContext) {
        enemyTime -= gctx.frameTime
        if (enemyTime > 0f) return

        // 생성 주기가 끝나면 적 5마리를 만든 뒤 다시 같은 간격으로 리셋한다.
        // wave 나 level 규칙은 아직 없으므로, 이번 단계에서는 같은 폭으로 고르게 배치한다.
        generate()
        enemyTime = GEN_INTERVAL
    }

    private fun generate() {
        val scene = gctx.scene as? MainScene ?: return
        val totalEnemyWidth = Enemy.ENEMY_WIDTH * COUNT_PER_WAVE
        val gap = (gctx.metrics.width - totalEnemyWidth) / (COUNT_PER_WAVE + 1)

        // 적 사이 간격과 화면 양끝 여백을 모두 같게 맞춘다.
        // x 는 중심점이므로 "왼쪽 여백 + 적 반너비"를 첫 적의 중심으로 사용한다.
        repeat(COUNT_PER_WAVE) {
            val x = gap + Enemy.ENEMY_WIDTH / 2f + it * (Enemy.ENEMY_WIDTH + gap)
            scene.world.add(Enemy(gctx, x), MainScene.Layer.ENEMY)
        }
    }

    override fun draw(canvas: Canvas) {
        // EnemyGenerator 는 화면에 직접 보이는 오브젝트가 아니라
        // "언제 적을 만들지"만 판단하는 담당자이므로 그릴 것은 없다.
    }

    companion object {
        const val GEN_INTERVAL = 3f
        // 이 값을 3, 4, 6 처럼 바꿔 보면서
        // 적 사이 간격과 화면 양끝 여백이 계속 같은 폭으로 유지되는지 확인해 보면 좋다.
        const val COUNT_PER_WAVE = 5
    }
}
