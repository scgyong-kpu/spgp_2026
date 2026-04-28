package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Rect
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.SheetSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// 플레이어는 상태마다 다른 프레임 Rect 집합을 쓰므로 SheetSprite 를 상속한다.
// RUN, JUMP, DOUBLE_JUMP 를 같은 Player 안에서 다루되, 실제 프레임 선택과 그리기는 SheetSprite 에 맡긴다.
class Player(gctx: GameContext) : SheetSprite(gctx, R.mipmap.cookie_player_sheet, 10f), IBoxCollidable {
    enum class State {
        // 지금은 RUN, JUMP, DOUBLE_JUMP 세 상태만 두고 시작한다.
        // 이후 Slide, Hit 같은 상태가 늘어나면 이 enum 에 계속 추가할 수 있다.
        RUN, JUMP, DOUBLE_JUMP, SLIDE,
    }

    private val stateRects = mapOf(
        State.RUN to RUN_RECTS,
        State.JUMP to JUMP_RECTS,
        State.DOUBLE_JUMP to DOUBLE_JUMP_RECTS,
        State.SLIDE to SLIDE_RECTS,
    )
    private val stateInsets = mapOf(
        State.RUN to INSETS_RUN,
        State.JUMP to INSETS_JUMP,
        State.DOUBLE_JUMP to INSETS_DOUBLE_JUMP,
        State.SLIDE to INSETS_SLIDE,
    )
    // stateRects 는 상태 이름과 프레임 Rect 목록을 연결해 둔 표다.
    // 상태가 바뀔 때 이 표를 통해 SheetSprite 가 그릴 프레임 묶음을 갈아끼운다.

    var state = State.RUN
        set(value) {
            field = value

            // 상태가 바뀌면 그 상태에 맞는 Rect 목록을 SheetSprite 에 넘긴다.
            // 이렇게 하면 상태 전환과 프레임 선택은 Player 가 담당하고,
            // 실제 그리기 루프는 SheetSprite 가 공통 처리한다.
            stateRects[value]?.let { frameRects = it }
            updateCollisionRect()
        }

    // 점프 중에는 위로 향하는 속도를 들고 있다가, 매 프레임 중력으로 줄여 나간다.
    var jumpSpeed = 0f
    // jumpSpeed 는 y 방향 이동량의 기반 값이다.
    // 음수면 위로 올라가고, 양수면 아래로 떨어지므로 점프 물리를 단순하게 표현할 수 있다.
    // collisionRect 는 실제 스프라이트보다 조금 작은 사각형을 따로 둔다.
    // 눈에 보이는 가장자리보다 안쪽에서 충돌해야 더 자연스럽게 느껴진다.
    override val collisionRect = RectF()

    init {
        // 처음에는 이동 로직 없이, 화면에 보이는 플레이어 위치와 크기만 잡아 둔다.
        // 이후 Jump, Slide, 상태 애니메이션을 Player 클래스 안에서 계속 확장할 수 있다.
        width = Player.WIDTH
        height = Player.HEIGHT
        setCenter(INIT_X, INIT_Y)
        state = State.RUN
        updateCollisionRect()
    }

    fun jump() {
        // jump() 는 현재 상태에 따라 서로 다른 점프를 만든다.
        // RUN 에서는 첫 점프, JUMP 에서는 더블 점프, DOUBLE_JUMP 에서는 이미 점프를 한 번 더 쓴 상태다.
        when (state) {
            State.RUN -> {
                // 달리는 상태에서 점프하면 JUMP 상태로 바뀌고, 점프 속도가 초기화된다.
                state = State.JUMP
                jumpSpeed = -JUMP_POWER
            }
            State.JUMP -> {
                // 점프 상태에서 점프하면 DOUBLE_JUMP 상태로 바뀌고, 점프 속도를 초기화한다.
                // 더블점프가 발에서 JUMP_POWER 만큼의 분사 로켓이라면 추가가 맞겠지만
                // 게임적으로는 JUMP_POWER 로 초기화 되는 것이 더 자연스럽다.
                state = State.DOUBLE_JUMP
                jumpSpeed = -JUMP_POWER
            }
            else -> {
                // 그 외 상태에서는 점프 입력을 받아도 추가로 상태를 바꾸지 않는다.
                return
            }
        }
    }

    fun slide(sliding: Boolean) {
        if (state == State.RUN) {
            if (sliding) {
                // 달리는 상태에서 슬라이드하면 SLIDE 상태로 바뀐다.
                state = State.SLIDE
            }
        } else if (state == State.SLIDE) {
            if (!sliding) {
                // 슬라이드 상태에서 슬라이드 입력이 끝나면 RUN 상태로 바뀐다.
                state = State.RUN
            }
        }
    }

    override fun update(gctx: GameContext) {
        // RUN 은 멈춰 있고, JUMP / DOUBLE_JUMP 는 같은 중력 루프로 처리한다.
        // 상태는 다르지만 실제 y 이동 규칙은 같으므로 물리 계산은 한곳에 모아 둔다.
        when (state) {
            State.JUMP, State.DOUBLE_JUMP -> {
                // 점프 상태에서는 y 위치를 위아래로 움직이는 간단한 테스트 로직을 넣어 둔다.
                // 이후에는 점프 시작 시점의 속도와 중력 가속도를 이용해 포물선 운동을 하는 로직으로 바뀌게 된다.
                y += jumpSpeed * gctx.frameTime
                if (y >= INIT_Y) {
                    y = INIT_Y
                    state = State.RUN
                    syncDstRect()
                    updateCollisionRect()
                    return
                }
                syncDstRect()
                updateCollisionRect()
                jumpSpeed += GRAVITY * gctx.frameTime
            }
            else -> {
                // 달리는 상태에 등에서는 별도 이동 로직이 없다.
            }
        }
    }

    private fun updateCollisionRect() {
        val insets = stateInsets[state] ?: return
        val left = insets[0] * width
        val top = insets[1] * height
        val right = insets[2] * width
        val bottom = insets[3] * height
        collisionRect.set(
            dstRect.left + width * insets[0],
            dstRect.top + height * insets[1],
            dstRect.right - width * insets[2],
            dstRect.bottom - height * insets[3],
        );
    }

    companion object {
        // 플레이어 크기는 일단 고정값으로 두고 시작한다.
        // 나중에는 화면 비율이나 가상 좌표계 기준으로 다시 정리할 수 있다.
        // INIT_X / INIT_Y 는 시작 위치, GRAVITY / JUMP_POWER 는 테스트용 점프 물리값이다.
        // 즉 여기 숫자들은 "지금은 테스트용"이라는 뜻을 코드 옆에서 바로 읽게 해 준다.
        const val WIDTH = 386f
        const val HEIGHT = 386f
        const val INIT_X = 200f
        const val INIT_Y = 510f
        const val GRAVITY = 1700f
        const val JUMP_POWER = 900f
        // 플레이어의 충돌 박스는 좌우 40f, 상하 20f 만큼 안쪽으로 줄인다.
        const val COLLISION_INSET_X = 40f
        const val COLLISION_INSET_Y = 20f
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
        val DOUBLE_JUMP_RECTS = listOf(
            Rect(274, 2, 544, 272),
            Rect(546, 2, 816, 272),
            Rect(818, 2, 1088, 272),
            Rect(1090, 2, 1360, 272),
        )
        val SLIDE_RECTS = listOf(
            Rect(2450, 2, 2720, 272),
            Rect(2722, 2, 2992, 272),
        )
        val INSETS_RUN = arrayOf(0.3f, 0.5f, 0.3f, 0.0f)
        val INSETS_JUMP = arrayOf(0.3f, 0.6f, 0.3f, 0.0f)
        val INSETS_DOUBLE_JUMP = arrayOf(0.3f, 0.6f, 0.3f, 0.0f)
        val INSETS_SLIDE = arrayOf(0.2f, 0.75f, 0.2f, 0.0f)
        val INSETS_FALLING = arrayOf(0.3f, 0.5f, 0.3f, 0.0f)
        val INSETS_HURT = arrayOf(0.3f, 0.50f, 0.4f, 0.0f)
    }
}
