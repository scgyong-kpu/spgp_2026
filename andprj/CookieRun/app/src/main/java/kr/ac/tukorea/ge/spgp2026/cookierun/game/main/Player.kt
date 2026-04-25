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

    // 점프 중에는 위로 향하는 속도를 들고 있다가, 매 프레임 중력으로 줄여 나간다.
    var jumpSpeed = 0f

    init {
        // 처음에는 이동 로직 없이, 화면에 보이는 플레이어 위치와 크기만 잡아 둔다.
        // 이후 Jump, Slide, 상태 애니메이션을 Player 클래스 안에서 계속 확장할 수 있다.
        width = Player.WIDTH
        height = Player.HEIGHT
        setCenter(INIT_X, INIT_Y)
        state = State.RUN
    }

    fun jump() {
        if (state != State.RUN) return
        state = State.JUMP
        jumpSpeed = -JUMP_POWER
    }

    override fun update(gctx: GameContext) {
        when (state) {
            State.RUN -> {
                // 달리는 상태에서는 별도 이동 로직이 없다.
                // 이후에는 달리는 속도에 맞춰 x 를 조금씩 증가시키는 로직이 들어갈 수 있다.
            }
            State.JUMP -> {
                // 점프 상태에서는 y 위치를 위아래로 움직이는 간단한 테스트 로직을 넣어 둔다.
                // 이후에는 점프 시작 시점의 속도와 중력 가속도를 이용해 포물선 운동을 하는 로직으로 바뀌게 된다.
                y += jumpSpeed * gctx.frameTime
                if (y >= INIT_Y) {
                    y = INIT_Y
                    state = State.RUN
                    syncDstRect()
                    return
                }
                syncDstRect()
                jumpSpeed += GRAVITY * gctx.frameTime
            }
        }
    }

    companion object {
        // 플레이어 크기는 일단 고정값으로 두고 시작한다.
        // 나중에는 화면 비율이나 가상 좌표계 기준으로 다시 정리할 수 있다.
        // INIT_X / INIT_Y 는 시작 위치, GRAVITY / JUMP_POWER 는 테스트용 점프 물리값이다.
        const val WIDTH = 386f
        const val HEIGHT = 386f
        const val INIT_X = 200f
        const val INIT_Y = 510f
        const val GRAVITY = 1700f
        const val JUMP_POWER = 900f
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
