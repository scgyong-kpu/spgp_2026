package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.ImageNumber
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class ScoreNumber(gctx: GameContext) : ImageNumber(
    gctx = gctx,
    mipmapId = R.mipmap.number_24x32,
    right = 850f,
    top = 50f,
    dstCharWidth = 60f,
) {
    init {
        // 점수판은 게임 시작 시 0 이 바로 보이는 편이 자연스럽다.
        setValueImmediately(0)
    }
}
