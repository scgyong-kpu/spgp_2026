package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Rect
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.SheetSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// 플레이어는 상태마다 다른 프레임 Rect 집합을 쓰므로 SheetSprite 를 상속한다.
// RUN 과 JUMP 를 같은 Player 안에서 다루되, 실제 프레임 선택과 그리기는 SheetSprite 에 맡긴다.
class Player(gctx: GameContext) : SheetSprite(gctx, R.mipmap.cookie_player_sheet, 10f) {
    enum class State {
        // 지금은 RUN, JUMP 두 상태만 두고 시작한다.
        // 이후 Slide, Hit 같은 상태가 늘어나면 이 enum 에 계속 추가할 수 있다.
        RUN, JUMP,
    }

    private val stateRects = mapOf(
        State.RUN to RUN_RECTS,
        State.JUMP to JUMP_RECTS,
    )

    var state = State.RUN
        set(value) {
            field = value

            // 상태가 바뀌면 그 상태에 맞는 Rect 목록을 SheetSprite 에 넘긴다.
            // 이렇게 하면 상태 전환과 프레임 선택은 Player 가 담당하고,
            // 실제 그리기 루프는 SheetSprite 가 공통 처리한다.
            stateRects[value]?.let { frameRects = it }
        }

    init {
        // 처음에는 이동 로직 없이, 화면에 보이는 플레이어 위치와 크기만 잡아 둔다.
        // 이후 Jump, Slide, 상태 애니메이션을 Player 클래스 안에서 계속 확장할 수 있다.
        width = Player.WIDTH
        height = Player.HEIGHT
        setCenter(200f, 510f)
        state = State.RUN
    }

    fun jump() {
        // 지금은 테스트 단계라 터치할 때 RUN 과 JUMP 를 서로 번갈아 바꾸기만 한다.
        // 실제 게임에서는 "땅에 있을 때만 점프 가능" 같은 조건이 여기에 들어가게 된다.
        when (state) {
            State.RUN -> state = State.JUMP
            State.JUMP -> state = State.RUN
        }
    }

    companion object {
        // 플레이어 크기는 일단 고정값으로 두고 시작한다.
        // 나중에는 화면 비율이나 가상 좌표계 기준으로 다시 정리할 수 있다.
        const val WIDTH = 386f
        const val HEIGHT = 386f
        val RUN_RECTS = listOf(
            Rect(2, 274, 272, 544),
            Rect(274, 274, 544, 544),
            Rect(546, 274, 816, 544),
            Rect(818, 274, 1088, 544),
        )
        val JUMP_RECTS = listOf(
            Rect(1906, 2, 2176, 272),
            Rect(2178, 2, 2448, 272),
        )
    }
}
