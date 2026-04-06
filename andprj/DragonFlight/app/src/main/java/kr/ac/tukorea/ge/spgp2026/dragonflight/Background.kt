package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 지금 단계의 Background 는 스크롤이나 타일링 없이,
// 배경 이미지를 화면 전체에 한 장 깔아 두는 가장 단순한 Sprite 이다.
// 나중에 scrolling background 나 TiledBackground 로 일반화하기 전,
// "배경 layer 가 실제로 먼저 그려진다"는 흐름을 확인하기 위한 시작점으로 둔다.
class Background(gctx: GameContext) : Sprite(gctx, R.mipmap.df_bg) {
    init {
        // 배경은 현재 가상 좌표계 전체를 덮도록 맞춘다.
        // setSize(), setCenter() helper 안에서 바로 syncDstRect() 가 이루어지므로
        // draw() 전에 따로 dstRect 를 다시 맞출 필요가 없다.
        setSize(gctx.metrics.width, gctx.metrics.height)
        setCenter(gctx.metrics.width / 2f, gctx.metrics.height / 2f)
    }
}
