package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.abs

class Player(val gctx: GameContext) : Sprite(gctx, R.mipmap.fighter) {
    // 먼저 화면에 보이는 기본 크기와 시작 위치만 override 해 둔다.
    // 나중에 Player 가 화면 경계를 벗어나지 않게 하거나,
    // 기체별 크기를 다르게 둘 때도 같은 방식으로 값을 바꿀 수 있다.
    override var width = PLAYER_WIDTH
    override var height = PLAYER_HEIGHT
    override var x = gctx.metrics.width / 2f
    override var y = gctx.metrics.height - PLAYER_HEIGHT

    // x 는 Sprite 의 중심점이므로,
    // 좌우 경계도 이미지 폭의 절반만큼 안쪽에서 계산해야 한다.
    val minPlayerX = PLAYER_WIDTH / 2f
    val maxPlayerX = gctx.metrics.width - PLAYER_WIDTH / 2f

    // targetX 는 "플레이어가 현재 향하고 있는 목표 x 좌표"이다.
    // 터치가 들어오면 screen 좌표를 가상 좌표계로 변환한 뒤 이 값으로 기억해 둔다.
    // update() 는 매 프레임마다 현재 x 에서 targetX 쪽으로 조금씩 이동한다.
    private var targetX = x

    // targetX 를 화면에서 확인하기 쉽도록, 조이스틱 thumb 이미지를 임시 마커로 재사용한다.
    // Player 가 아직 target 위치에 도달하지 않은 동안만 이 위치를 그려 준다.
    private val targetBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.tu_joystick_thumb)
    private val targetRect = RectF()

    override fun update(gctx: GameContext) {
        // 이번 단계에서는 "방향"보다 "목표 위치"를 먼저 저장해 두고,
        // 그 목표를 향해 일정 속도로 가까워지는 방식으로 움직인다.
        //
        // step 은 이번 프레임에 최대로 이동할 수 있는 거리이다.
        // frameTime 을 곱해 두면 기기 성능이 달라도 1초 기준 속도는 비슷하게 유지된다.
        val step = SPEED * gctx.frameTime
        val delta = targetX - x

        // 남은 거리가 이번 프레임 이동 가능 거리보다 작으면
        // 지나쳐 버리지 않도록 targetX 에 정확히 맞춘다.
        x = when {
            delta > step -> x + step
            delta < -step -> x - step
            else -> targetX
        }

        // 좌우 경계는 미리 계산해 둔 minPlayerX, maxPlayerX 범위 안으로 다시 맞춘다.
        // 지금은 화면 폭(gctx.metrics.width)과 플레이어 폭을 이용해 경계를 계산하므로,
        // 해상도나 가상 좌표계 폭이 달라져도 같은 방식으로 동작한다.
        x = x.coerceIn(minPlayerX, maxPlayerX)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // 이미 targetX 에 거의 도달했다면 target 마커는 굳이 그리지 않는다.
        if (abs(targetX - x) < 0.5f) return

        // Player 가 향하고 있는 목표 x 위치를 시각적으로 확인하기 위한 임시 표시이다.
        // y 는 Player 와 같은 높이에 두고, thumb 이미지를 작은 마커처럼 그린다.
        targetRect.set(
            targetX - TARGET_MARKER_SIZE / 2f,
            y - TARGET_MARKER_SIZE / 2f,
            targetX + TARGET_MARKER_SIZE / 2f,
            y + TARGET_MARKER_SIZE / 2f,
        )
        canvas.drawBitmap(targetBitmap, null, targetRect, null)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        // 이번 단계에서는 터치한 "화면 위치"가 아니라
        // 플레이어가 이동해야 할 "가상 좌표계 안의 목표 위치"가 중요하다.
        // 그래서 screen 좌표를 그대로 비교하지 않고,
        // metrics.fromScreen() 으로 virtual x 로 변환한 뒤 targetX 에 저장한다.
        val pt = gctx.metrics.fromScreen(event.x, event.y)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                targetX = pt.x.coerceIn(minPlayerX, maxPlayerX)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {}
        }
        return true
    }

    companion object {
        const val SPEED = 300f
        const val PLAYER_WIDTH = 144f
        const val PLAYER_HEIGHT = 160f
        const val TARGET_MARKER_SIZE = 72f
    }
}
