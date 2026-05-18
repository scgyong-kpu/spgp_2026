package kr.ac.tukorea.ge.spgp2026.a2dg.util

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.graphics.withTranslation

// Gauge 는 life bar, cooltime bar 처럼
// "0.0 ~ 1.0 진행률을 선 하나로 보여 주는" 가장 단순한 HUD 도구이다.
//
// 내부에서는 길이 1.0 기준의 가로선을 그려 두고,
// draw(..., scale, ...) 에서 scale 을 곱해 실제 화면 길이로 키운다.
// 이렇게 하면 같은 Gauge 객체를 Enemy life, Player cooltime 같은 여러 용도로 재사용하기 쉽다.
//
// 생성자 인자 thickness 는 gauge 의 "전체 길이"가 아니라
// 선을 얼마나 두껍게 그릴지 정하는 stroke width 값이다.
// 실제 길이는 draw(..., scale, ...) 의 scale 이 담당하고,
// thickness 는 그 gauge 가 얇게 보일지 두껍게 보일지를 담당한다고 보면 된다.
//
// 내부 기준 길이가 1.0 이므로, thickness 는 "전체 길이 1.0 대비 두께"라고 볼 수도 있다.
// 예를 들어 thickness 0.1f 는 gauge 전체 길이의 10% 두께라는 뜻이다.
//
// 예를 들어 thickness 가 0.1f 이면,
// scale 이 100f 일 때 화면에서는 대략 두께 10 정도의 gauge 로 보인다.
//
// Gauge 자신은 현재 life 나 cooltime 같은 "상태값"을 저장하지 않는다.
// 생성 시에는 색과 두께 같은 모양 정보만 받아 두고,
// draw() 를 호출하는 쪽이 그때그때 progress 값을 넘겨 주는 방식이다.
class Gauge(
    thickness: Float,
    fgColor: Int,
    bgColor: Int,
) {
    // 배경선은 더 굵고 옅게,
    // 진행선은 더 가늘고 진하게 두면 작은 크기에서도 gauge 모양이 잘 보인다.
    private val fgPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = thickness / 2f
        color = fgColor
        strokeCap = Paint.Cap.ROUND
    }

    private val bgPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = thickness
        color = bgColor
        strokeCap = Paint.Cap.ROUND
    }

    // x, y 는 gauge 를 그리기 시작할 기준 위치이고,
    // scale 은 "기준 길이 1.0 짜리 gauge"를 화면에서 얼마나 길게 보일지 결정한다.
    // 예를 들어 scale 을 100f 로 주면 실제로는 길이 100 의 gauge 처럼 보인다.
    fun draw(canvas: Canvas, x: Float, y: Float, scale: Float, progress: Float) {
        canvas.withTranslation(x, y) {
            scale(scale, scale)
            draw(this, progress)
        }
    }

    // progress 는 0.0 ~ 1.0 범위를 기대한다.
    // 먼저 배경선 전체를 그리고, 그 위에 실제 진행률만큼만 앞쪽 선을 덧그린다.
    fun draw(canvas: Canvas, progress: Float) {
        canvas.drawLine(0f, 0f, 1f, 0f, bgPaint)
        if (progress > 0f) {
            // 1.0 보다 큰 값이 들어와도 gauge 길이를 넘지 않게 clamp 한다.
            canvas.drawLine(0f, 0f, progress.coerceAtMost(1f), 0f, fgPaint)
        }
    }
}
