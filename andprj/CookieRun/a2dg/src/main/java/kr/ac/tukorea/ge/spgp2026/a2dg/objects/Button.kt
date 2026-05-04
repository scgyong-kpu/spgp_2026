package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Button(
    private val gctx: GameContext,
    resId: Int,
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    private val onTouch: (pressed: Boolean) -> Boolean,
) : Sprite(gctx, resId), ITouchable {
    init {
        setCenter(centerX, centerY)
        setSize(width, height)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val point = gctx.metrics.fromScreen(event.x, event.y)
                if (!dstRect.contains(point.x, point.y)) {
                    return false
                }

                onTouch(true)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onTouch(false)
            }
            // DOWN 을 처리한 Button 에 이후 event 를 계속 보내는 capture 책임은 Scene 이 가진다.
            // Button 은 MOVE 같은 중간 event 를 소비했다고 알려 capture 가 유지되도록만 한다.
            else -> true
        }
    }
}
