package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sqrt

// JoyStick 은 가상 패드 입력을 위한 공통 게임 오브젝트이다.
// 배경/thumb 리소스와 위치, 크기는 게임마다 다를 수 있으므로 생성자에서 주입받는다.
class JoyStick(
    private val gctx: GameContext,
    bgResId: Int,
    thumbResId: Int,
    centerX: Float,
    centerY: Float,
    private val bgRadius: Float,
    private val thumbRadius: Float,
) : IGameObject {
    private val bgBitmap: Bitmap = gctx.res.getBitmap(bgResId)
    private val thumbBitmap: Bitmap = gctx.res.getBitmap(thumbResId)

    // x, y 가 음수이면 각각 오른쪽, 아래쪽 경계에서부터의 상대 거리로 해석한다.
    // 예를 들어 centerX = -220f 이면 "오른쪽에서 220 떨어진 곳"이 된다.
    private val resolvedCenterX = if (centerX >= 0f) centerX else gctx.metrics.width + centerX
    private val resolvedCenterY = if (centerY >= 0f) centerY else gctx.metrics.height + centerY
    private val maxRadius = bgRadius - thumbRadius

    private val bgRect = RectF(
        resolvedCenterX - bgRadius,
        resolvedCenterY - bgRadius,
        resolvedCenterX + bgRadius,
        resolvedCenterY + bgRadius,
    )

    private val thumbRect = RectF(
        resolvedCenterX - thumbRadius,
        resolvedCenterY - thumbRadius,
        resolvedCenterX + thumbRadius,
        resolvedCenterY + thumbRadius,
    )

    private var isVisible = false
    private var thumbX = resolvedCenterX
    private var thumbY = resolvedCenterY
    private var downX = resolvedCenterX
    private var downY = resolvedCenterY

    // angle 은 중심에서 thumb 로 향하는 방향을 radian 값으로 저장한다.
    // power 는 thumb 가 maxRadius 대비 얼마나 멀리 나가 있는지를 0.0~1.0 범위로 저장한다.
    var angle = 0f
        private set
    var power = 0f
        private set

    fun onTouchEvent(event: MotionEvent): Boolean {
        val pt = gctx.metrics.fromScreen(event.x, event.y)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isVisible = true
                downX = pt.x
                downY = pt.y
                resetThumb()
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = pt.x - downX
                val dy = pt.y - downY
                updateThumbPosition(dx, dy)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isVisible = false
                resetThumb()
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

    // ACTION_DOWN 시점으로부터 얼마나 움직였는지를 dx, dy 로 받아 처리한다.
    // 상대 이동 벡터의 길이를 구하고,
    // 그 길이가 maxRadius 원 밖이면 angle 을 유지한 채 원 경계 위로 다시 보정한다.
    private fun updateThumbPosition(dx: Float, dy: Float) {
        // Kotlin 함수 파라미터는 Java 의 final 지역변수처럼 다시 대입할 수 없다.
        // 그래서 "먼저 들어온 dx, dy 를 받아 두고, 필요하면 원 경계에 맞게 값을 다시 고쳐 쓸"
        // 중간 변수로 아래의 var dx, var dy 를 하나 더 만든다.
        var dx = dx
        var dy = dy
        var radius = sqrt(dx * dx + dy * dy)

        // atan2(dy, dx) 는 x축 양의 방향을 기준으로 현재 벡터가 어느 방향을 향하는지를
        // radian 값으로 돌려준다.
        angle = atan2(dy, dx)
        if (radius > maxRadius) {
            // thumb 가 원 밖으로 나가면, 같은 angle 방향을 유지한 채
            // 반지름만 maxRadius 인 원 경계 위 점으로 다시 맞춘다.
            dx = maxRadius * cos(angle)
            dy = maxRadius * kotlin.math.sin(angle)
            radius = maxRadius
        }

        // power 는 현재 thumb 가 중심에서 얼마나 멀리 나가 있는지를
        // 0.0~1.0 범위 비율로 바꾼 값이다.
        power = (radius / maxRadius).coerceIn(0f, 1f)
        Log.d(javaClass.simpleName, "angle=${"%.2f".format(angle)} power=${"%.2f".format(power)}")
        thumbX = resolvedCenterX + dx
        thumbY = resolvedCenterY + dy
    }

    private fun resetThumb() {
        thumbX = resolvedCenterX
        thumbY = resolvedCenterY
        angle = 0f
        power = 0f
    }
}
