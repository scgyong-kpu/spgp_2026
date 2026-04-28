package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.game.main.MainScene.Layer
import kotlin.random.Random

class MapLoader(gctx: GameContext, val world: World<Layer>): IGameObject {
    // floorRight 는 "아직 바닥을 더 배치할 수 있는 가장 오른쪽 위치"를 뜻한다.
    // itemRight 는 "아직 아이템을 더 배치할 수 있는 가장 오른쪽 위치"를 뜻한다.
    // update() 에서 이 값들이 화면 오른쪽 끝보다 작아지는 동안 새 오브젝트를 계속 추가한다.
    var itemRight = 0f
    var floorRight = 0f

    override fun update(gctx: GameContext) {
        while (floorRight < gctx.metrics.width) {
            // 바닥은 두 종류 중 하나를 고른다.
            // 이렇게 하면 같은 바닥만 반복되지 않고 맵의 모양이 조금씩 달라진다.
            val floorType = if (Random.nextBoolean()) Floor.Type.T_10x2 else Floor.Type.T_2x2
            val floor = Floor.get(gctx, floorType, floorRight, 700f)
            world.add(floor, Layer.FLOOR)
            // 새 바닥을 추가했으면, 다음 바닥은 지금 바닥의 오른쪽 끝에서 이어 붙인다.
            floorRight += floor.width
        }
        while (itemRight < gctx.metrics.width) {
            // itemRight 는 아이템이 배치된 가장 오른쪽 위치다. 이 위치가 화면을 넘어갈 때까지 새 아이템을 추가한다.
            // 이렇게 하면 화면에 보이는 범위만큼 아이템이 배치되어서, 스크롤 시 아이템이 갑자기 나타나는 일이 줄어든다.
            if (Random.nextInt(5) == 0) {
                // 20% 확률로 젤리 대신 바닥 타일을 하나 더 추가한다. 바닥 타일은 바닥 레이어에 추가한다.
                // 아이템과 바닥을 완전히 분리하지 않고 섞어 두면, 같은 맵이라도 조금 더 변화가 생긴다.
                val y = (1 .. 5).random() * 100f
                val floor = Floor.get(gctx, Floor.Type.T_3x1, itemRight, y)
                world.add(floor, Layer.FLOOR)
                itemRight += floor.width
                continue
            }
            // Magnification 효과를 확인하기 전까지는 특수 젤리가 비교적 자주 나오게 둔다.
            // 10% 정도면 플레이 중 금방 확인할 수 있고, 나중에 실제 맵 데이터로 바꾸기도 쉽다.
            val itemIndex = if (Random.nextInt(10) == 0) {
                JellyItem.MAGNIFICATION_INDEX
            } else {
                Random.nextInt(JellyItem.JELLY_COUNT)
            }
            // 아이템은 여러 높이 중 하나에 놓는다.
            // 현재는 100px 단위 레인에서 무작위로 고르는 간단한 방식이다.
            val y = (0 .. 6).random() * 100f
            val item = JellyItem.get(gctx, itemIndex, itemRight, y)
            world.add(item, Layer.ITEM)
            // 아이템도 오른쪽으로 이어 붙이되, 한 개의 폭만큼 다음 시작점을 옮긴다.
            itemRight += item.width
        }

        // 맵과 함께 오른쪽 경계도 왼쪽으로 이동시켜야,
        // 다음 아이템이 "화면 밖에서 미리 생성되는" 구조가 된다.
        floorRight += MapObject.SPEED * gctx.frameTime
        itemRight += MapObject.SPEED * gctx.frameTime
    }

    override fun draw(canvas: Canvas) {
    }
}
