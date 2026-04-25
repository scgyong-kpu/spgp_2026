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
// dstRect 는 x, y, width, height 로부터 만들어지는 파생 상태이다.
// DragonFlight 쪽 Sprite 는 draw() 에서 매번 syncDstRect() 를 하지 않으므로,
// 하위 클래스가 x, y, width, height 를 직접 대입하는 패턴을 쓴다면
// init 블록 마지막에서 syncDstRect() 를 한 번 호출해야 한다.
// 순서는 "x/y/width/height 값을 모두 넣은 뒤 마지막에 syncDstRect()" 가 되어야 한다.
// 중간에 먼저 sync 하면, 아직 덜 들어간 값 기준 사각형이 만들어질 수 있다.
// 다만 Kotlin 은 "프로퍼티 초기화 -> 모든 init 블록" 순서가 아니라,
// 클래스 본문에 적힌 순서대로 프로퍼티 초기화와 init 블록이 섞여서 실행된다.
// init 블록도 여러 개 둘 수 있다.
// 그래서 override var x = ..., override var y = ..., override var width = ..., override var height = ...
// 같은 선언과 init 블록이 섞여 있다면, syncDstRect() 는 그 네 값 초기화보다 "뒤에 있는" init 블록에서 호출해야 한다.
// 가장 안전한 패턴은 관련 프로퍼티 선언을 먼저 모아 두고, 그 다음 init 블록 마지막에서 syncDstRect() 를 호출하는 것이다.
// 반대로 init 블록 안에서 값을 직접 대입하는 패턴이라면, 그 init 블록의 마지막 줄에서 호출해야 한다.
//
// 반대로 setCenter(), setSize(), setCenterProportionalWidth(), setCenterProportionalHeight() 같은 helper 를 쓰면
// 그 안에서 바로 syncDstRect() 가 호출된다.
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
    // Kotlin 에서는 클래스와 멤버가 기본적으로 final 이다.
    // 그래서 하위 클래스가 width = ... 처럼 프로퍼티를 override 하게 하려면 open 을 붙여야 한다.
    // Java 로 치면 field 자체를 override 하는 것이 아니라, getter/setter 메서드를 override 가능하게 여는
    // 감각에 가깝다고 보면 된다.
    open var x = 0f
    open var y = 0f

    // 화면에 그릴 크기이다.
    // bitmap 원본 크기와는 별개의 값이며, 하위 클래스가 자기 의미에 맞게 설정한다.
    // 예를 들어 Ball 은 SIZE, Fighter 는 FIGHTER_SIZE 를 넣을 수 있다.
    open var width = 0f
    open var height = 0f

    // 현재 bitmap 자체가 가진 원본 픽셀 크기이다.
    // 화면에 그릴 width, height 와는 다른 개념이므로 별도 이름으로 분리한다.
    val bitmapWidth: Int
        get() = bitmap.width

    val bitmapHeight: Int
        get() = bitmap.height

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, srcRect, dstRect, null)
    }

    // 중심점 x, y 를 같이 바꾸는 가장 기본 helper 이다.
    // 좌표 두 값을 이 helper 로 바꿀 때는 곧바로 dstRect 도 다시 맞춘다.
    fun setCenter(centerX: Float, centerY: Float) {
        x = centerX
        y = centerY
        syncDstRect()
    }

    // width, height 를 같이 바꿀 때 한 줄로 묶어 적기 위한 helper 이다.
    // 중심점 기준 Sprite 에서는 크기 두 값을 짝으로 다루는 경우가 많으므로
    // setSize() 를 두면 "크기만 바꾸는 의도"가 더 또렷하게 읽힌다.
    // 이 helper 를 사용할 때도 dstRect 가 바로 최신 상태가 되도록 syncDstRect() 를 함께 호출한다.
    fun setSize(width: Float, height: Float) {
        this.width = width
        this.height = height
        syncDstRect()
    }

    // Spark, 아이템, 한 장짜리 적처럼 "중심은 정해져 있고 가로폭만 먼저 정하고 싶다"는 경우를 위한 helper 이다.
    // 세로 크기는 현재 bitmap 원본 비율을 유지하도록 자동 계산한다.
    // 매 프레임 자주 도는 setCenter() 에 분기 로직을 넣지 않고, 비율 유지가 필요한 호출만 별도 helper 로 분리한다.
    fun setCenterProportionalWidth(centerX: Float, centerY: Float, width: Float) {
        x = centerX
        y = centerY
        this.width = width
        this.height = width * bitmapHeight / bitmapWidth.toFloat()
        syncDstRect()
    }

    // 세로 크기를 먼저 정하고 싶을 때는 이 helper 를 쓴다.
    // 예를 들어 세로 이펙트, 기둥형 오브젝트, 화면 높이 기준으로 맞추는 UI 이미지는
    // height 를 먼저 정하는 쪽이 더 읽기 쉬울 수 있다.
    fun setCenterProportionalHeight(centerX: Float, centerY: Float, height: Float) {
        x = centerX
        y = centerY
        this.height = height
        this.width = height * bitmapWidth / bitmapHeight.toFloat()
        syncDstRect()
    }

    // 중심점 x, y 와 width, height 를 좌상단/우하단 좌표로 풀어
    // Canvas 가 이해하는 RectF 형태로 다시 맞춘다.
    fun syncDstRect() {
        dstRect.set(
            x - width / 2f,
            y - height / 2f,
            x + width / 2f,
            y + height / 2f,
        )
    }
}
