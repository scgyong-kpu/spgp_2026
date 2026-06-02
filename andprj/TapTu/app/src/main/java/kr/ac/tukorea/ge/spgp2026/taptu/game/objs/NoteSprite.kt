package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.data.Note
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.mainWorld

// NoteSprite 는 Note data 하나를 화면에 보이는 Sprite 하나로 바꾼다.
// note 가 가진 time 과 현재 음악 시간의 차이를 y 좌표로 변환해,
// 음악이 진행될수록 goal line 을 향해 아래로 내려오게 한다.
class NoteSprite(
    gctx: GameContext,
    private val note: Note,
    private val musicTimeProvider: () -> Float,
) : Sprite(gctx, R.mipmap.note_1) {
    init {
        val x = xFromPret(note.pret)
        setSize(WIDTH, HEIGHT)
        updatePosition(x)
    }

    override fun update(gctx: GameContext) {
        updatePosition(x)
        if (y > gctx.metrics.height + HEIGHT) {
            gctx.mainWorld().remove(this, MainLayer.NOTE)
        }
    }

    private fun updatePosition(x: Float) {
        val y = yFromTime(note.time, musicTimeProvider())
        setCenter(x, y)
    }

    companion object {
        private const val X_SPACE = 130f
        private const val LEFT = 450f - 2 * X_SPACE
        private const val WIDTH = 120f
        private const val HEIGHT = 55f
        const val GOAL_Y = 1400f

        // 이번 단계에서 가장 중요한 실험 상수이다.
        // note.time 과 musicTime 은 second 단위 Float 이고,
        // 두 값의 차이에 TIME_TO_Y 와 speed 를 곱한 만큼 GOAL_Y 위쪽에 배치한다.
        // 즉 TIME_TO_Y = 50f 라면 기본 배속에서 "음악 시간 1초 차이"가 화면에서는 "50 game unit 차이"로 보인다.
        const val TIME_TO_Y = 50f
        var speed = 1.0f

        fun screenfulTime(): Float {
            return (GOAL_Y + HEIGHT) / unitsPerSecond()
        }

        private fun xFromPret(pret: Int): Float {
            // pret 0~4 는 5개의 lane 을 뜻한다.
            // 기본 가상 폭 900 에서 중앙 x=450 을 기준으로, 양쪽으로 X_SPACE 만큼 벌려
            // 190, 320, 450, 580, 710 위치에 note 를 놓는다.
            return LEFT + pret * X_SPACE
        }

        private fun yFromTime(noteTime: Float, musicTime: Float): Float {
            return GOAL_Y - (noteTime - musicTime) * unitsPerSecond()
        }

        private fun unitsPerSecond(): Float {
            return TIME_TO_Y * speed
        }
    }
}
