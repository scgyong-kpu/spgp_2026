package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Player(gctx: GameContext) : Sprite(gctx, R.mipmap.fighter) {
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

    // dx 는 "이번 프레임에 어느 방향으로 움직일지"만 간단히 나타낸다.
    // -1 이면 왼쪽, +1 이면 오른쪽, 0 이면 정지이다.
    private var dx = 0f

    // lastTouchX 는 "직전 MOVE 에서의 x 좌표"를 기억한다.
    // 이번 단계에서는 처음 DOWN 했던 위치보다,
    // 바로 직전 좌표와 비교해서 왼쪽으로 가는지 오른쪽으로 가는지를 알아내는 것이 더 중요하다.
    // 예를 들어 50 -> 40 까지 갔다가 41 이 되면,
    // 41 은 최초 down 좌표 50 보다 여전히 작지만 직전 좌표 40 보다는 크다.
    // 따라서 그 순간부터는 오른쪽(+1)으로 방향이 바뀌었다고 판단해야 한다.
    private var lastTouchX = 0f

    override fun update(gctx: GameContext) {
        // 현재 구현은 좌/우 한 축만 먼저 다룬다.
        // dx 가 -1, 0, +1 중 하나이므로
        // SPEED * frameTime 으로 나온 이동량에 그 방향만 곱해 주면 된다.
        x += SPEED * gctx.frameTime * dx

        // 좌우 경계는 미리 계산해 둔 minPlayerX, maxPlayerX 범위 안으로 다시 맞춘다.
        // 지금은 화면 폭(gctx.metrics.width)과 플레이어 폭을 이용해 경계를 계산하므로,
        // 해상도나 가상 좌표계 폭이 달라져도 같은 방식으로 동작한다.
        x = x.coerceIn(minPlayerX, maxPlayerX)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        // 이번 단계에서는 "직전 터치보다 왼쪽으로 갔는가, 오른쪽으로 갔는가"만 판단하면 된다.
        // 즉 절대적인 가상 좌표값은 중요하지 않고, x 값의 대소 관계만 중요하다.
        // 그래서 screen -> virtual 좌표 변환은 굳이 하지 않고 event.x 를 그대로 사용한다.
        val x = event.x
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // DOWN 순간에는 아직 이동 방향이 없으므로 dx 는 0 이고,
                // 다음 MOVE 와 비교할 수 있도록 현재 x 만 기억해 둔다.
                lastTouchX = x
                dx = 0f
            }
            MotionEvent.ACTION_MOVE -> {
                if (x < lastTouchX - TOUCH_THRESHOLD) {
                    // 몇 픽셀 정도의 미세한 흔들림은 무시하고,
                    // threshold 를 넘을 만큼 충분히 왼쪽으로 움직였을 때만 왼쪽으로 판단한다.
                    dx = -1f
                } else if (x > lastTouchX + TOUCH_THRESHOLD) {
                    // 오른쪽도 같은 방식으로 threshold 를 넘을 때만 반응한다.
                    dx = 1f
                } else {
                    // threshold 안쪽의 작은 움직임은 떨림으로 보고,
                    // 새 방향으로 바꾸지 않고 원래 가던 방향을 그대로 유지한다.
                }
                // 이번 x 를 다음 MOVE 와 비교할 새 기준점으로 저장한다.
                // 그래서 방향이 바뀌는 순간의 x 가 자동으로 새 기준이 된다.
                lastTouchX = x
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 손을 떼면 이동 방향도 0 이 되어 정지한다.
                dx = 0f
            }
        }
        return true
    }

    companion object {
        const val SPEED = 300f
        const val TOUCH_THRESHOLD = 10f
        const val PLAYER_WIDTH = 144f
        const val PLAYER_HEIGHT = 160f
    }
}
