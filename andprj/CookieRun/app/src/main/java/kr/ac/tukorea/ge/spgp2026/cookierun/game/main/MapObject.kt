package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// MapObject 는 CookieRun 맵을 이루는 오브젝트들의 공통 부모다.
// Floor, JellyItem, Obstacle 처럼 "맵 위에 놓이는 요소"를 한 가지 이름으로 묶어 두면
// Scene 이나 Factory 에서 다루기 쉬워진다.
// 지금은 Sprite 에 별도 동작을 추가하지 않고, 타입 묶음 역할만 먼저 담당한다.
abstract class MapObject(
    gctx: GameContext,
    resId: Int,
) : Sprite(gctx, resId)
