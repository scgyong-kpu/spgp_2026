package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Canvas
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.game.main.MainScene.Layer

class MapLoader(gctx: GameContext, val world: World<Layer>): IGameObject {
    // itemRight 는 "아직 아이템을 더 배치할 수 있는 가장 오른쪽 위치"를 뜻한다.
    // update() 에서 이 값이 화면 오른쪽 끝보다 작아지는 동안 아이템을 계속 추가한다.
    var itemRight = 0f
    init {
        // 바닥은 한 장짜리 배경이 아니라, 여러 종류의 플랫폼 타일을 흩뿌려 두는 식으로 구성한다.
        // Floor 는 클래스 내부의 고정 속도(SPEED)로 왼쪽으로 자동 스크롤한다.
        world.add(Floor(gctx, Floor.Type.T_10x2).apply {
            setLeftTop(0f, 700f)
        }, Layer.FLOOR)
        world.add(Floor(gctx, Floor.Type.T_2x2).apply {
            setLeftTop(1000f, 700f)
        }, Layer.FLOOR)
        world.add(Floor(gctx, Floor.Type.T_3x1).apply {
            setLeftTop(500f, 300f)
        }, Layer.FLOOR)
        world.add(Floor(gctx, Floor.Type.T_3x1).apply {
            setLeftTop(1000f, 500f)
        }, Layer.FLOOR)
    }

    override fun update(gctx: GameContext) {
        while (itemRight < gctx.metrics.width) {
            // itemRight 는 아이템이 배치된 가장 오른쪽 위치다. 이 위치가 화면을 넘어갈 때까지 새 아이템을 추가한다.
            // 이렇게 하면 화면에 보이는 범위만큼 아이템이 배치되어서, 스크롤 시 아이템이 갑자기 나타나는 일이 줄어든다.
            val itemIndex = (0 until JellyItem.JELLY_COUNT).random()
            val item = JellyItem(gctx, itemIndex)
            val y = (0 .. 6).random() * 100f
            item.setLeftTop(itemRight, y)
            Log.d(javaClass.simpleName, "Adding $item")
            world.add(item, Layer.ITEM)
            itemRight += item.width
        }

        // 맵과 함께 오른쪽 경계도 왼쪽으로 이동시켜야,
        // 다음 아이템이 "화면 밖에서 미리 생성되는" 구조가 된다.
        itemRight += MapObject.SPEED * gctx.frameTime
    }

    override fun draw(canvas: Canvas) {
    }
}
