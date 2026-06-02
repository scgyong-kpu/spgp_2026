package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.data.Note

// NoteSprite 는 Note data 하나를 화면에 보이는 Sprite 하나로 바꾼다.
// 아직 음악 시간에 맞춰 움직이지 않고, note 가 가진 pret / time 값을 좌표로 변환해 배치만 한다.
// 이 단계를 먼저 두면 "데이터 파일을 읽었다"에서 바로 "화면 좌표로 보인다"까지 확인할 수 있다.
class NoteSprite(
    gctx: GameContext,
    note: Note,
) : Sprite(gctx, R.mipmap.note_1) {
    init {
        val x = xFromPret(note.pret)
        val y = yFromTime(note.time)
        setCenter(x, y)
        setSize(WIDTH, HEIGHT)
    }

    companion object {
        private const val X_SPACE = 130f
        private const val LEFT = 450f - 2 * X_SPACE
        private const val WIDTH = 120f
        private const val HEIGHT = 55f

        // 이번 단계에서 가장 중요한 실험 상수이다.
        // note.time 은 second 단위 Float 이고, 이 값에 TIME_TO_Y 를 곱해 y 좌표로 바꾼다.
        // 즉 TIME_TO_Y = 50f 라면 "음악 시간 1초 차이"가 화면에서는 "50 game unit 차이"로 보인다.
        const val TIME_TO_Y = 50f

        private fun xFromPret(pret: Int): Float {
            // pret 0~4 는 5개의 lane 을 뜻한다.
            // 기본 가상 폭 900 에서 중앙 x=450 을 기준으로, 양쪽으로 X_SPACE 만큼 벌려
            // 190, 320, 450, 580, 710 위치에 note 를 놓는다.
            return LEFT + pret * X_SPACE
        }

        private fun yFromTime(time: Float): Float {
            return time * TIME_TO_Y
        }
    }
}
