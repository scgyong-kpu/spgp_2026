package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.graphics.toColorInt
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Label
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class ScoreLabel : IGameObject {
    private val label = Label(
        textSize = 80f,
        color = "#9D00FF".toColorInt(),
        align = Paint.Align.RIGHT,
        typeface = Typeface.MONOSPACE,
    )

    // 지금 단계의 ScoreLabel 은 점수 애니메이션 없이
    // 현재 score 값을 그대로 화면에 표시하는 가장 작은 HUD 객체이다.
    private var score = 0

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        // RIGHT 정렬이므로 x=850 을 기준으로 숫자의 오른쪽 끝이 맞춰진다.
        // score 가 0 이어도 "0"을 그대로 보여 주어 HUD 가 비어 보이지 않게 한다.
        label.draw(canvas, score.toString(), 850f, 100f)
    }

    fun add(amount: Int) {
        score += amount
    }

    fun getScore(): Int {
        return score
    }
}
