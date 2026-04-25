package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// 플레이어 캐릭터는 달리기/점프 같은 애니메이션이 필요하므로 AnimSprite 를 상속한다.
// cookie_player_run 이미지는 720x200 크기 안에 180x200 프레임 4개가 가로로 이어진 strip 이다.
// 따라서 frameCount 를 4로 명시해, 1초에 10프레임 속도로 4장의 run 프레임이 반복되게 만든다.
class Player(gctx: GameContext) : AnimSprite(gctx, R.mipmap.cookie_player_run, 10f, frameCount = 4) {
    enum class State {
        // 지금은 RUN, JUMP 두 상태만 두고 시작한다.
        // 이후 Slide, Hit 같은 상태가 늘어나면 이 enum 에 계속 추가할 수 있다.
        RUN, JUMP,
    }

    init {
        // 처음에는 이동 로직 없이, 화면에 보이는 플레이어 위치와 크기만 잡아 둔다.
        // 이후 Jump, Slide, 상태 애니메이션을 Player 클래스 안에서 계속 확장할 수 있다.
        width = Player.WIDTH
        height = Player.HEIGHT
        setCenter(200f, 700f)
    }

    var state = State.RUN
        set(value) {
            field = value

            // 상태가 바뀌면 사용할 이미지와 프레임 수도 함께 바뀐다.
            // 지금은 RUN 과 JUMP 만 구분하지만, 나중에 상태가 늘어나도
            // 이 when 문 안에서 "상태 -> 리소스/프레임 수" 규칙을 한곳에 모아 둘 수 있다.
            val (resId, frameCount) = when (value) {
                State.RUN -> R.mipmap.cookie_player_run to 4
                State.JUMP -> R.mipmap.cookie_player_jump to 2
            }

            // 새 상태에 맞는 비트맵을 가져오고,
            // AnimSprite 가 프레임 폭과 높이를 다시 계산하도록 frameCount 도 함께 갱신한다.
            bitmap = gctx.res.getBitmap(resId)
            this.frameCount = frameCount
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
        const val WIDTH = 180f
        const val HEIGHT = 200f
    }
}
