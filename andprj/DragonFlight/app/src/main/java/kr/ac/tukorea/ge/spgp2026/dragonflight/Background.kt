package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 지금 단계의 Background 는 스크롤이나 타일링 없이,
// 배경 이미지를 화면 전체에 한 장 깔아 두는 가장 단순한 Sprite 이다.   
// 나중에 scrolling background 나 TiledBackground 로 일반화하기 전,
// "배경 layer 가 실제로 먼저 그려진다"는 흐름을 확인하기 위한 시작점으로 둔다.
class Background(gctx: GameContext) : Sprite(gctx, R.mipmap.df_bg) {
    init {
        // 지금 단계의 배경은 "화면 가로폭에 맞춰 비율 유지"를 먼저 확인하는 용도이다.
        // 그래서 새 helper 인 setCenterProportionalWidth() 를 써서
        // 중심은 화면 가운데에 두고, 가로폭만 metrics.width 에 맞춘다.
        setCenterProportionalWidth(
            gctx.metrics.width / 2f,
            gctx.metrics.height / 2f,
            gctx.metrics.width,
        )
    }
}
