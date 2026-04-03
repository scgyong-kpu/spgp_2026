package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// ImageNumber 는 0~9 숫자가 가로로 붙어 있는 bitmap 한 장을 이용해
// 정수를 HUD 숫자처럼 그리는 공통 객체이다.
//
// 숫자 자체는 value 가 들고 있고,
// 화면에 보이는 값은 displayValue 가 들고 있어
// update() 에서 천천히 따라가게 만들 수 있다.
open class ImageNumber(
    gctx: GameContext,
    mipmapId: Int,
    private val right: Float,
    private val top: Float,
    private val dstCharWidth: Float,
) : IGameObject {
    // 다른 Sprite 류 객체들처럼 생성 시 GameContext 를 받아
    // 필요한 bitmap 을 현재 게임의 공통 리소스 캐시에서 가져온다.
    // 이렇게 하면 app 쪽에서 BitmapPool 을 따로 꺼내 넘기지 않아도 된다.
    private val bitmap = gctx.res.getBitmap(mipmapId)
    private val srcRect = Rect()
    private val dstRect = RectF()
    private val srcCharWidth = bitmap.width / 10
    private val srcCharHeight = bitmap.height
    private val dstCharHeight = dstCharWidth * srcCharHeight / srcCharWidth

    var value = 0
    private var displayValue = 0

    // value 는 바깥에서 직접 읽고 쓸 수 있는 "목표 숫자"이다.
    // 반면 화면에 실제로 보이는 값은 private displayValue 가 따로 들고 있어
    // update() 에서 조금씩 따라가며 애니메이션된다.

    // 처음 배치할 때 0 으로 맞추거나,
    // 저장된 값을 불러온 직후처럼 애니메이션 없이 즉시 숫자를 맞추고 싶을 때 사용한다.
    fun setValueImmediately(value: Int) {
        this.value = value
        this.displayValue = value
    }

    override fun update(gctx: GameContext) {
        // value 와 displayValue 를 나누는 이유는
        // 점수가 갑자기 크게 늘어났을 때도 화면 숫자가 한 번에 바뀌지 않고
        // 조금씩 따라가며 증가하는 느낌을 주기 위해서이다.
        val diff = value - displayValue
        if (diff == 0) return

        // 차이가 아주 작으면 한 자리씩 움직이고,
        // 차이가 크면 10분의 1 만큼 따라가게 해
        // 큰 점수 변화도 너무 오래 걸리지 않게 한다.
        displayValue += when {
            diff in -9..-1 -> -1
            diff in 1..9 -> 1
            else -> diff / 10
        }
    }

    override fun draw(canvas: Canvas) {
        // 0 도 점수판에 보이게 하기 위해 적어도 한 자리 숫자는 그린다.
        var current = displayValue
        var x = right

        do {
            // 숫자 이미지 시트는 0~9 가 가로로 같은 폭으로 붙어 있다고 가정한다.
            // 현재 자리수 digit 에 맞는 영역만 srcRect 로 잘라 한 자리씩 그린다.
            val digit = current % 10
            srcRect.set(
                digit * srcCharWidth,
                0,
                (digit + 1) * srcCharWidth,
                srcCharHeight,
            )

            // right 정렬 HUD 이므로, 오른쪽 자리부터 왼쪽으로 한 칸씩 물러나며 그린다.
            x -= dstCharWidth
            dstRect.set(x, top, x + dstCharWidth, top + dstCharHeight)
            canvas.drawBitmap(bitmap, srcRect, dstRect, null)
            current /= 10
        } while (current > 0)
    }
}
