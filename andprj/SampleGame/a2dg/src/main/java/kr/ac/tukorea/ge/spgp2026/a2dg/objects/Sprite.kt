package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// Sprite 는 비트맵 하나를 가지는 가장 단순한 GameObject 기반 클래스이다.
// 게임 로직에서 다루기 쉬운 중심점 x, y 와 화면에 그릴 width, height 를 직접 멤버로 둔다.
//
// 여기서 width, height 는 "현재 bitmap 원본 크기"가 아니라,
// 게임 안에서 이 Sprite 를 화면에 얼마 크기로 그릴지를 나타내는 값이다.
// 따라서 부모 Sprite 가 bitmap.width, bitmap.height 로 자동 초기화하지 않고,
// Ball, Fighter 같은 하위 클래스가 자기 의미에 맞는 크기를 직접 정하도록 열어 둔다.
//
// 반대로 bitmap 자체의 원본 픽셀 크기가 필요할 수도 있으므로,
// bitmapWidth, bitmapHeight 프로퍼티를 따로 제공해 두었다.
//
// 실제 draw() 에서는 x, y, width, height 로부터 dstRect 를 다시 계산한 뒤
// canvas.drawBitmap() 을 호출한다.
// 회전이나 일부 영역만 그리기 같은 더 복잡한 경우는 하위 클래스에서 draw() 를 override 하면 된다.
open class Sprite(
    gctx: GameContext,
    resId: Int,
) : IGameObject {
    // 나중에 AnimSprite 같은 클래스에서 프레임마다 다른 bitmap 으로 바꿀 수도 있으므로 var 로 둔다.
    protected var bitmap: Bitmap = gctx.res.getBitmap(resId)

    // null 이면 bitmap 전체를 그린다.
    // SheetSprite 나 AnimSprite 는 이 값을 적절히 바꿔 일부 영역만 그릴 수 있다.
    protected var srcRect: Rect? = null

    // 실제 Canvas 에 drawBitmap() 할 때 사용할 목적 사각형이다.
    // x, y, width, height 에서 매번 다시 계산하므로 바깥에서는 직접 만지지 않게 protected 로 둔다.
    protected val dstRect = RectF()

    // x, y 는 Sprite 의 중심점이다.
    // 이동, 거리 계산, 회전 중심 처리 같은 게임 로직을 조금 더 직관적으로 쓰기 위해
    // left/top 대신 center 기준 좌표를 공통 상태로 사용한다.
    var x = 0f
    var y = 0f

    // 화면에 그릴 크기이다.
    // bitmap 원본 크기와는 별개의 값이며, 하위 클래스가 자기 의미에 맞게 설정한다.
    // 예를 들어 Ball 은 SIZE, Fighter 는 FIGHTER_SIZE 를 넣을 수 있다.
    var width = 0f
    var height = 0f

    // 현재 bitmap 자체가 가진 원본 픽셀 크기이다.
    // 화면에 그릴 width, height 와는 다른 개념이므로 별도 이름으로 분리한다.
    val bitmapWidth: Int
        get() = bitmap.width

    val bitmapHeight: Int
        get() = bitmap.height

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        syncDstRect()
        canvas.drawBitmap(bitmap, srcRect, dstRect, null)
    }

    protected fun setCenter(centerX: Float, centerY: Float) {
        x = centerX
        y = centerY
    }

    // 중심점 x, y 와 width, height 를 좌상단/우하단 좌표로 풀어
    // Canvas 가 이해하는 RectF 형태로 다시 맞춘다.
    protected fun syncDstRect() {
        dstRect.set(
            x - width / 2f,
            y - height / 2f,
            x + width / 2f,
            y + height / 2f,
        )
    }
}
