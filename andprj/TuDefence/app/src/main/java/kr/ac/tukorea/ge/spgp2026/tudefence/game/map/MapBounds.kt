package kr.ac.tukorea.ge.spgp2026.tudefence.game.map

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

interface MapBounds {
    val mapWidth: Float
    val mapHeight: Float
}

fun GameContext.mapBounds(): MapBounds {
    // Shell 같은 map 위 객체는 "현재 main world 의 크기"만 알면 되고,
    // 그 크기를 누가 계산했는지(MainScene 인지 TiledBackground 인지)는 알 필요가 없다.
    // Scene.world 의 public type 이 World<*>? 이므로 여기서 MapBounds 약속으로 한 번 좁힌다.
    return scene.world as MapBounds
}
