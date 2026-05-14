package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.weapon

import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R

class Cannon(gctx: GameContext): Sprite(gctx, R.mipmap.cannon_bg_256) {
    private val barrelRect = RectF()
    private val barrelBitmap = gctx.res.getBitmap(R.mipmap.barrel_512)

    init {
        setSize(BASE_SIZE, BASE_SIZE)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // 포탑 몸체는 Sprite 의 dstRect 에 맞춰 그리고,
        // 포신은 같은 중심을 기준으로 별도 bitmap 을 얹는다.
        // 다음 단계에서 이 중심을 기준으로 barrel 만 회전시킬 예정이다.
        barrelRect.set(
            x - BARREL_SIZE / 2f,
            y - BARREL_SIZE / 2f,
            x + BARREL_SIZE / 2f,
            y + BARREL_SIZE / 2f,
        )
        canvas.withRotation(130f, x, y) {
            canvas.drawBitmap(barrelBitmap, null, barrelRect, null)
        }
    }

    companion object {
        private const val BASE_SIZE = 100f
        private const val BARREL_SIZE = 110f
    }
}
