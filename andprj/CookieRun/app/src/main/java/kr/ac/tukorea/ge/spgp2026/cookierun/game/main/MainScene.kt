package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.HorzScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

class MainScene(gctx: GameContext) : Scene(gctx) {
    // 예전처럼 0, 1 같은 Int 로 레이어를 구분할 수도 있지만,
    // enum 을 쓰면 각 레이어의 의미가 이름으로 드러나서 읽기와 유지보수가 쉬워진다.
    enum class Layer {
        BG, PLAYER
    }
    override val clipsRect = true
    override val world = World(Layer.entries.toTypedArray()).apply {
        // 배경은 가장 뒤 레이어에 두고, 플레이어는 그 앞 레이어에 둔다.
        // 처음에는 임시 Sprite 를 바로 만들 수도 있지만, Player 클래스로 한 번 감싸 두면
        // 이후 입력, 애니메이션, 충돌 같은 책임을 Player 쪽에 자연스럽게 붙여 갈 수 있다.
        add(HorzScrollBackground(gctx, R.mipmap.cookie_run_bg_1, -50f), Layer.BG)
        add(HorzScrollBackground(gctx, R.mipmap.cookie_run_bg_2, -100f), Layer.BG)
        add(HorzScrollBackground(gctx, R.mipmap.cookie_run_bg_3, -150f), Layer.BG)
        add(Player(gctx), Layer.PLAYER)
    }
}
