package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R

class Call(gctx: GameContext): Sprite(gctx, R.mipmap.calls) {
    enum class Type {
        perfect, great, good, bad, miss,
    }

    companion object {
        fun typeWithTimeDiff(time: Float): Type {
            var absTime = time
            if (absTime < 0) absTime = -absTime

            if (absTime < 0.1f) return Type.perfect
            if (absTime < 0.2f) return Type.great
            if (absTime < 0.3f) return Type.good
            if (absTime < 0.4f) return Type.bad
            return Type.miss
        }
    }
}