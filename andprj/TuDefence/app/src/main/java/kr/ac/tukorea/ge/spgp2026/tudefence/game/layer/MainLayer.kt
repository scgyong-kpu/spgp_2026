package kr.ac.tukorea.ge.spgp2026.tudefence.game.layer

import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

enum class MainLayer {
    BG,
    WEAPON,
    ENEMY,
    SHELL,
    EXPLOSION,
    CONTROLLER,
}

@Suppress("UNCHECKED_CAST")
fun GameContext.mainWorld(): World<MainLayer> {
    // Scene.world 의 public type 은 World<*>? 이므로, layer type 정보는 여기서 한 번 되살린다.
    // 이 helper 는 "현재 scene 은 MainLayer 를 쓰는 main game scene 이다"라는 약속을 모아두는 곳이다.
    // 덕분에 Fly/Shell/Explosion 같은 obj package 객체들이 MainScene class 를 직접 알 필요가 줄어든다.
    return scene.world as World<MainLayer>
}
