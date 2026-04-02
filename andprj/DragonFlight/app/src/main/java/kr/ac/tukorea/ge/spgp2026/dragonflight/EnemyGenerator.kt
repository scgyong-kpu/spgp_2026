package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class EnemyGenerator(
    private val gctx: GameContext,
) : IGameObject {
    private var enemyTime = 0f
    private var wave = 0
    private var nextLevel = 1

    override fun update(gctx: GameContext) {
        enemyTime -= gctx.frameTime
        if (enemyTime > 0f) return

        // 생성 주기가 끝날 때마다 wave 를 1 올리고 적 5마리를 만든다.
        // 지금 단계에서는 wave 가 올라갈수록 "속도"가 조금씩 빨라진다.
        wave++
        generate()
        enemyTime = GEN_INTERVAL
    }

    private fun generate() {
        val scene = gctx.scene as? MainScene ?: return
        val totalEnemyWidth = Enemy.ENEMY_WIDTH * COUNT_PER_WAVE
        val gap = (gctx.metrics.width - totalEnemyWidth) / (COUNT_PER_WAVE + 1)

        // wave 1 일 때는 기본 속도, 그 다음 wave 부터는 SPEED_STEP 만큼씩 더 빠르게 한다.
        // 즉 wave 가 높아질수록 적이 더 빨리 내려오게 된다.
        val speed = Enemy.DEFAULT_SPEED + (wave - 1) * SPEED_STEP

        // 한 줄에 5마리를 같은 폭으로 배치한다.
        // 적 사이 간격과 화면 양끝 여백을 모두 같게 맞추고,
        // x 는 중심점이므로 "왼쪽 여백 + 적 반너비"를 첫 적의 중심으로 사용한다.
        repeat(COUNT_PER_WAVE) {
            val x = gap + Enemy.ENEMY_WIDTH / 2f + it * (Enemy.ENEMY_WIDTH + gap)

            // 아직 최종 level 공식은 없으므로,
            // 임시 정수형 변수 nextLevel 을 1..20 범위에서 1씩 증가시키며 순환 사용한다.
            // 이렇게 하면 enemy_01 부터 enemy_20 까지 strip 이 실제로 모두 연결됐는지 한눈에 확인할 수 있다.
            scene.world.add(Enemy(gctx, x, level = nextLevel, speed = speed), MainScene.Layer.ENEMY)
            nextLevel = if (nextLevel == Enemy.MAX_LEVEL_COUNT) 1 else nextLevel + 1
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
        // wave 가 하나 올라갈 때마다 속도를 얼마나 더할지 정하는 값이다.
        const val SPEED_STEP = 20f
    }
}
