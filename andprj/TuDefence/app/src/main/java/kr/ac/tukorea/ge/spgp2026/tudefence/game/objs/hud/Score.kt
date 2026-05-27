package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.hud

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.ImageNumber
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R

// Score 는 화면 오른쪽 위에 표시되는 점수판이다.
// 실제 숫자 그리기는 a2dg 의 ImageNumber 가 담당하고,
// 이 클래스는 "점수 값을 어떻게 바꾸고 어디에 배치할지"만 감싼다.
//
// 작년 TuDefence 에서는 gold_number.png 를 이용해 같은 방식으로 점수를 그렸으므로,
// 올해도 같은 숫자 스트립을 가져와 그대로 사용한다.
class Score(gctx: GameContext) : IGameObject {
    private val number = ImageNumber(
        gctx,
        R.mipmap.gold_number,
        gctx.metrics.width - RIGHT_MARGIN,
        TOP_MARGIN,
        DIGIT_WIDTH,
    )

    fun add(amount: Int) {
        number.value += amount
    }

    fun setScore(value: Int) {
        number.setValueImmediately(value)
    }

    val value: Int
        get() = number.value

    override fun update(gctx: GameContext) {
        number.update(gctx)
    }

    override fun draw(canvas: Canvas) {
        number.draw(canvas)
    }

    companion object {
        private const val RIGHT_MARGIN = 30f
        private const val TOP_MARGIN = 30f
        private const val DIGIT_WIDTH = 50f
    }
}
