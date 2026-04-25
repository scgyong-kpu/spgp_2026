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

    // Scene 경계 바깥은 그리지 않도록 잘라서(drawing clip) 불필요한 오버드로우를 줄인다.
    override val clipsRect = true

    // World 는 레이어 순서대로 그려진다.
    // 여기서는 BG -> PLAYER 순서이므로 배경 뒤에 플레이어가 올라오는 구성이 된다.
    override val world = World(Layer.entries.toTypedArray()).apply {
        // (배경 리소스, 스크롤 속도) 쌍을 한 번에 선언해 반복 추가한다.
        // speed 가 음수면 오른쪽에서 왼쪽으로 이동한다.
        // 앞쪽 레이어일수록 절댓값을 크게 주면 parallax(원근감) 효과가 난다.
        listOf(
            R.mipmap.cookie_run_bg_1 to -50f,
            R.mipmap.cookie_run_bg_2 to -100f,
            R.mipmap.cookie_run_bg_3 to -150f,
        ).forEach { (resId, speed) ->
            // 같은 코드 패턴으로 배경을 추가하므로 유지보수가 쉽다.
            // 배경 장수를 늘릴 때는 위 리스트에 항목만 추가하면 된다.
            add(HorzScrollBackground(gctx, resId, speed), Layer.BG)
        }
        // a to b 는 Pair(a, b) 와 같다. to 연산자 덕분에 가독성이 좋아진다.

        // 플레이어는 배경보다 앞 레이어에 배치한다.
        add(Player(gctx), Layer.PLAYER)
    }
}
