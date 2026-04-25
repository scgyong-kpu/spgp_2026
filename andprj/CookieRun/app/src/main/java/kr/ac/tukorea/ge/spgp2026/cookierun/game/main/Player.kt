package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// 플레이어 캐릭터는 달리는 애니메이션이 필요하므로 AnimSprite 를 상속한다.
// 이미지가 720x200 크기에 프레임이 4개이므로, 10f 의 애니메이션 속도로 4프레임이 1초에 한 바퀴씩 돌아가게 된다.
// 정사각형이 아니어서 frameCount 를 주지 않으면 프레임 하나가 200x200 크기로 해석되어 버리므로, frameCount=4 로 명시적으로 지정한다.
// 임시로 frameCount 를 0 으로 해서 애니메이션이 깨져 나오는 것을 확인한다. 이후에 애니메이션이 제대로 나오는지 확인하기 위해 frameCount=4 로 수정한다.
class Player(gctx: GameContext): AnimSprite(gctx, R.mipmap.cookie_player_run, 10f) { //, frameCount = 4) {
    init {
        // 처음에는 움직임이나 애니메이션 없이, 화면에 보이는 플레이어 위치와 크기만 잡아 둔다.
        // 이후 Jump, Slide, 상태 애니메이션을 붙여도 Player 클래스 안에서 이어서 확장할 수 있다.
        width = Player.WIDTH
        height = Player.HEIGHT
        setCenter(200f, 700f)
    }

    companion object {
        // 플레이어 크기는 고정값으로 선언해 둔다. (실제 게임에서는 화면 크기에 비례해서 정하는 게 좋다.)
        const val WIDTH = 180f
        const val HEIGHT = 200f
    }
}
