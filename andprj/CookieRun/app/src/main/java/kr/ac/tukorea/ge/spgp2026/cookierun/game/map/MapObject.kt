package kr.ac.tukorea.ge.spgp2026.cookierun.game.map

import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.game.main.MainScene

// MapObject 는 CookieRun 맵을 이루는 오브젝트들의 공통 부모다.
// Floor, JellyItem, Obstacle 처럼 "맵 위에 놓이는 요소"를 한 가지 이름으로 묶어 두면
// Scene 이나 Factory 에서 다루기 쉬워진다.
// 맵 오브젝트는 공통적으로 왼쪽으로 흐르므로, 기본 이동 로직도 여기서 함께 처리한다.
abstract class MapObject(
    gctx: GameContext,
    resId: Int,
) : Sprite(gctx, resId), IRecyclable, IBoxCollidable {
    // MapObject 는 더 이상 생성자에서 left/top/width/height 를 받지 않는다.
    // Floor, JellyItem, Obstacle 처럼 하위 클래스마다 배치 기준이 조금씩 다르고,
    // 재활용된 객체도 같은 init() 함수에서 위치와 크기를 다시 잡아야 하기 때문이다.
    //
    // 따라서 MapObject 는 공통 이동/제거/충돌 인터페이스만 제공하고,
    // 실제 dstRect 설정은 각 하위 클래스의 init() 에서 담당한다.

    // 각 맵 오브젝트는 자기 타입이 속한 레이어를 알아야
    // 화면 밖으로 나갔을 때 World 에서 자기 자신을 제거할 수 있다.
    abstract val layer: MainScene.Layer

    override val collisionRect: RectF
        get() = dstRect

    // 생성 후 위치를 초기화하도록 한다. 이 함수는 재활용 된 뒤에도 불릴 예정이다
    fun setLeftTop(left: Float, top: Float) {
        // left/top 을 기준으로 dstRect 를 바로 옮기므로,
        // 타일 배치 시 중심점과 폭/높이를 따로 계산하지 않아도 된다.
        dstRect.offsetTo(left, top)
    }

    override fun update(gctx: GameContext) {
        // 맵 위에 있는 오브젝트들은 모두 같은 속도로 왼쪽으로 이동한다.
        // 개별 클래스는 배치나 표시 방식만 다루고, 공통 이동은 여기서 처리한다.
        // 중심점 x 를 쓰지 않고 dstRect 만으로 위치를 관리한다.
        // 이렇게 하면 배치 시점과 스크롤 시점이 같은 사각형을 공유해서,
        // 타일 이동과 화면 밖 판정이 더 단순해진다.

        val dx = SPEED * gctx.frameTime
        dstRect.offset(dx, 0f)
        if (dstRect.right < 0f) {
            // 화면 왼쪽 바깥으로 완전히 나간 오브젝트는 World 에서 제거한다.
//            Log.d(javaClass.simpleName, "Removed after leaving screen: $this")
            val scene = gctx.scene as MainScene
            scene.world.remove(this, layer)
        }
    }

    override fun onRecycle() {
    }
    companion object {
        // 맵 오브젝트의 공통 스크롤 속도다.
        // 음수 값이므로 왼쪽으로 흐른다.
        const val SPEED = -300f
    }
}
