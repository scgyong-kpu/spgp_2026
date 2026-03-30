package kr.ac.tukorea.ge.scgyong.samplegame.app

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.IGameObject
import kr.ac.tukorea.ge.scgyong.samplegame.R
import kotlin.math.sqrt

// JoyStick 은 가상 패드 입력을 위한 게임 오브젝트이다.
// 이번 단계에서는 사용자가 추가한 joystick_bg, joystick_thumb 이미지를 이용해
// 화면에 기본 모양만 보이도록 만든다.
// 이번 커밋에서는 터치 메시지를 JoyStick 이 직접 받아
// Touch Down 인 동안만 보이게 처리하고,
// 아직 thumb 이동이나 angle/power 계산은 붙이지 않는다.
class JoyStick(private val gctx: GameContext) : IGameObject {
    private val bgBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.joystick_bg)
    private val thumbBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.joystick_thumb)

    // 일단은 화면 왼쪽 아래에 고정된 기본 위치에 그려 둔다.
    private val centerX = 220f
    private val centerY = gctx.metrics.height - 220f

    private val bgRadius = 180f
    private val thumbRadius = 90f
    private val maxRadius = bgRadius - thumbRadius

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

    private var isVisible = false
    private var thumbX = centerX
    private var thumbY = centerY
    private var downX = centerX
    private var downY = centerY

    fun onTouchEvent(event: MotionEvent): Boolean {
        val pt = gctx.metrics.fromScreen(event.x, event.y)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isVisible = true
                downX = pt.x
                downY = pt.y
                thumbX = centerX
                thumbY = centerY
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = pt.x - downX
                val dy = pt.y - downY
                updateThumbPosition(centerX + dx, centerY + dy)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isVisible = false
                thumbX = centerX
                thumbY = centerY
            }
        }
        return true
    }

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        thumbRect.set(
            thumbX - thumbRadius,
            thumbY - thumbRadius,
            thumbX + thumbRadius,
            thumbY + thumbRadius,
        )
        canvas.drawBitmap(bgBitmap, null, bgRect, null)
        canvas.drawBitmap(thumbBitmap, null, thumbRect, null)
    }

    // thumb 중심이 배경 중심에서 maxRadius 를 넘지 않도록 원형 범위 안으로 제한한다.
    // maxRadius 를 bgRadius - thumbRadius 로 두면
    // thumb 의 가장자리가 배경 원 바깥으로 튀어나가지 않게 막을 수 있다.
    private fun updateThumbPosition(x: Float, y: Float) {
        val dx = x - centerX
        val dy = y - centerY
        val distance = sqrt(dx * dx + dy * dy)
        if (distance <= maxRadius || distance == 0f) {
            thumbX = x
            thumbY = y
            return
        }

        val scale = maxRadius / distance
        thumbX = centerX + dx * scale
        thumbY = centerY + dy * scale
    }
}
