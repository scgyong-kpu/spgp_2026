package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.VertScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 작년 ForestTiledBg 처럼, 게임 전용 배경 클래스는 app 쪽에 두고
// 공통 배경 동작은 a2dg 의 VertScrollBackground 가 맡게 한다.
// 이미지 종류는 이 클래스가 고정하고, 실제 스크롤 속도는 바깥 Scene 이 넘겨 주게 한다.
class ForestBackground(gctx: GameContext, speed: Float) : VertScrollBackground(gctx, R.mipmap.df_bg, speed)
