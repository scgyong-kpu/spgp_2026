package kr.ac.tukorea.ge.spgp2026.a2dg.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

// LabelUtil 은 문자열을 그리는 가장 단순한 HUD 도구이다.
// Gauge 가 진행률을 그리는 공통 도구라면, LabelUtil 은 문자열을 그리는 공통 도구라고 보면 된다.
//
// LabelUtil 자신은 현재 score 나 wave 같은 상태를 저장하지 않고,
// 생성 시에는 글자 모양만 받아 두고 draw() 를 호출하는 쪽이 그때그때 문자열과 위치를 넘겨 준다.
class LabelUtil(
    textSize: Float,
    color: Int,
    align: Paint.Align = Paint.Align.LEFT,
    typeface: Typeface? = null,
) {
    // LabelUtil 은 "어떻게 그릴지"만 들고 있고,
    // "무슨 문자열을 그릴지"는 draw() 때마다 받는다.
    private val paint = Paint().apply {
        this.textSize = textSize
        this.color = color
        this.textAlign = align
        this.typeface = typeface
        isAntiAlias = true
    }

    // x, y 는 Canvas.drawText() 와 같은 기준점이다.
    // LEFT 정렬이면 x 가 왼쪽 시작점이 되고,
    // CENTER 정렬이면 x 가 문자열의 가운데,
    // RIGHT 정렬이면 x 가 문자열의 오른쪽 끝 기준이 된다.
    fun draw(canvas: Canvas, text: String, x: Float, y: Float) {
        canvas.drawText(text, x, y, paint)
    }
}
