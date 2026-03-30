package kr.ac.tukorea.ge.scgyong.samplegame.app

import android.graphics.Canvas
import android.util.Log
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.Sprite
import kr.ac.tukorea.ge.scgyong.samplegame.R

private const val FIGHTER_X = 450f
private const val FIGHTER_Y = 1200f
private const val FIGHTER_SIZE = 250f
private const val FIGHTER_ANGLE_OFFSET = 90f
private const val FIGHTER_SPEED = 500f

class Fighter(gctx: GameContext) : Sprite(gctx, R.mipmap.plane_240) {
    private var angleDegree = -FIGHTER_ANGLE_OFFSET
    private var targetX = FIGHTER_X
    private var targetY = FIGHTER_Y
    // dx, dy 는 이번 프레임에 더할 값이 아니라, 초당 몇 unit 씩 움직여야 하는지를 저장하는 속도 벡터이다.
    private var dx = 0f
    private var dy = -FIGHTER_SPEED

    init {
        width = FIGHTER_SIZE
        height = FIGHTER_SIZE
        setPosition(FIGHTER_X, FIGHTER_Y, appliesAngle = false)
    }

    fun setTarget(x: Float, y: Float) {
        targetX = x
        targetY = y

        // 현재 위치에서 목표 지점까지의 방향 벡터를 구한다.
        val dx = targetX - this.x
        val dy = targetY - this.y
        val distance = sqrt(dx * dx + dy * dy)
        if (distance == 0f) return

        // 방향 벡터를 단위 벡터로 만든 뒤, FIGHTER_SPEED 를 곱해 초당 속도 벡터로 바꿔 저장한다.
        this.dx = dx / distance * FIGHTER_SPEED
        this.dy = dy / distance * FIGHTER_SPEED

        // 전투기가 바라볼 방향도 목표 지점을 향하도록 각도를 함께 계산해 둔다.
        angleDegree = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        Log.d(javaClass.simpleName, "angleDegree: ${"%.2f".format(angleDegree)}")
    }

    private fun setPosition(x: Float, y: Float, appliesAngle: Boolean = true) {
        if (appliesAngle) {
            // 이전 위치에서 새 위치로 향하는 방향을 각도로 바꿔, 나중에 전투기 회전에 쓸 수 있게 둔다.
            val dx = (x - this.x).toDouble()
            val dy = (y - this.y).toDouble()
            angleDegree = Math.toDegrees(atan2(dy, dx)).toFloat()
            Log.d(javaClass.simpleName, "angleDegree: ${"%.2f".format(angleDegree)}")
        }
        this.x = x
        this.y = y
    }

    override fun update(gctx: GameContext) {
        // 목표 지점까지 얼마나 남았는지 본다.
        val remainingDx = targetX - x
        val remainingDy = targetY - y
        if (remainingDx == 0f && remainingDy == 0f) {
            return
        }

        // 이번 프레임 동안 x, y 축으로 각각 얼마나 움직일지를 계산한다.
        val frameDx = this.dx * gctx.frameTime
        val frameDy = this.dy * gctx.frameTime

        // 어느 한 축이라도 이번 프레임 이동량이 남은 거리보다 크면 그 축만 목표값에 붙인다.
        val nextX = if (abs(remainingDx) <= abs(frameDx)) targetX else x + frameDx
        val nextY = if (abs(remainingDy) <= abs(frameDy)) targetY else y + frameDy

        setPosition(nextX, nextY, appliesAngle = false)
    }

    override fun draw(canvas: Canvas) {
        canvas.withRotation(angleDegree + FIGHTER_ANGLE_OFFSET, x, y) {
            // save, rotate, restore 가 자동으로 일어나는 withRotation 블록 안에서 그리면,
            // 각도 만큼 회전된 상태로 그려진다. 실행 결과는 같고, 표현만 간결해 진다.
            syncDstRect()
            drawBitmap(bitmap, srcRect, dstRect, null)
        }
    }
}
