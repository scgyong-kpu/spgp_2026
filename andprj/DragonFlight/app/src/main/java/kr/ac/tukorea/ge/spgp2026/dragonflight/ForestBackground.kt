package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.VertScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 작년 ForestTiledBg 처럼, 게임 전용 배경 클래스는 app 쪽에 두고
// 공통 배경 동작은 a2dg 의 VertScrollBackground 가 맡게 한다.
// 지금은 이전 Background 가 쓰던 df_bg 이미지를 그대로 연결해서,
// 세로로 반복 배치되는 공통 배경 클래스가 실제로 잘 보이는지만 먼저 확인한다.
class ForestBackground(gctx: GameContext) : VertScrollBackground(gctx, R.mipmap.df_bg)
