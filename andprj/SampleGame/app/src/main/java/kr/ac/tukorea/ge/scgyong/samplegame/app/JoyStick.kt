package kr.ac.tukorea.ge.scgyong.samplegame.app

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.IGameObject
import kr.ac.tukorea.ge.scgyong.samplegame.R

// JoyStick 은 가상 패드 입력을 위한 게임 오브젝트이다.
// 이번 단계에서는 사용자가 추가한 joystick_bg, joystick_thumb 이미지를 이용해
// 화면에 기본 모양만 보이도록 만든다.
// 아직 터치 처리나 thumb 이동, angle/power 계산은 붙이지 않는다.
class JoyStick(gctx: GameContext) : IGameObject {
    private val bgBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.joystick_bg)
    private val thumbBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.joystick_thumb)

    // 일단은 화면 왼쪽 아래에 고정된 기본 위치에 그려 둔다.
    private val centerX = 220f
    private val centerY = gctx.metrics.height - 220f

    private val bgRadius = 180f
    private val thumbRadius = 90f

    private val bgRect = RectF(
        centerX - bgRadius,
        centerY - bgRadius,
        centerX + bgRadius,
        centerY + bgRadius,
    )

    private val thumbRect = RectF(
        centerX - thumbRadius,
        centerY - thumbRadius,
        centerX + thumbRadius,
        centerY + thumbRadius,
    )

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bgBitmap, null, bgRect, null)
        canvas.drawBitmap(thumbBitmap, null, thumbRect, null)
    }
}
