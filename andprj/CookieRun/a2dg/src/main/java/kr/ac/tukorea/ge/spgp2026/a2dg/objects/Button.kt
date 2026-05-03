package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Button(
    gctx: GameContext,
    resId: Int,
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    private val onTouch: (pressed: Boolean) -> Boolean,
) : Sprite(gctx, resId) {
    // ACTION_DOWN 이 버튼 안에서 시작되면 captures 를 true 로 둔다.
    // 이렇게 해야 Slide 버튼처럼 DOWN 이후 손가락이 버튼 밖으로 조금 움직여도
    // ACTION_UP 을 같은 버튼이 받아서 pressed=false 를 전달할 수 있다.
    private var captures = false

    init {
        setCenter(centerX, centerY)
        setSize(width, height)
    }

    fun onTouchEvent(gctx: GameContext, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val point = gctx.metrics.fromScreen(event.x, event.y)
                if (!dstRect.contains(point.x, point.y)) {
                    return false
                }

                captures = true
                onTouch(true)
            }
            MotionEvent.ACTION_UP -> {
                if (!captures) {
                    return false
                }

                captures = false
                onTouch(false)
            }
            else -> captures
        }
    }
}
