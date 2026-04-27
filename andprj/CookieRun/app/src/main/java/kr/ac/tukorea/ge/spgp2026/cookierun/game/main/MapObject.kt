package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// MapObject 는 CookieRun 맵을 이루는 오브젝트들의 공통 부모다.
// Floor, JellyItem, Obstacle 처럼 "맵 위에 놓이는 요소"를 한 가지 이름으로 묶어 두면
// Scene 이나 Factory 에서 다루기 쉬워진다.
// 맵 오브젝트는 공통적으로 왼쪽으로 흐르므로, 기본 이동 로직도 여기서 함께 처리한다.
abstract class MapObject(
    gctx: GameContext,
    resId: Int,
) : Sprite(gctx, resId) {
    // 각 맵 오브젝트는 자기 타입이 속한 레이어를 알아야
    // 화면 밖으로 나갔을 때 World 에서 자기 자신을 제거할 수 있다.
    abstract val layer: MainScene.Layer

    override fun update(gctx: GameContext) {
        // 맵 위에 있는 오브젝트들은 모두 같은 속도로 왼쪽으로 이동한다.
        // 개별 클래스는 배치나 표시 방식만 다루고, 공통 이동은 여기서 처리한다.
        x += SPEED * gctx.frameTime
        syncDstRect()
        if (dstRect.right < 0f) {
            // 화면 왼쪽 바깥으로 완전히 나간 오브젝트는 World 에서 제거한다.
            Log.d(javaClass.simpleName, "Removed after leaving screen: $this")
            val scene = gctx.scene as MainScene
            scene.world.remove(this, layer)
        }
    }

    companion object {
        // 맵 오브젝트의 공통 스크롤 속도다.
        // 음수 값이므로 왼쪽으로 흐른다.
        const val SPEED = -300f
    }
}
