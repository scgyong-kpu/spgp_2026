package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.game.main.MainScene.Layer

class MapLoader(gctx: GameContext, val world: World<Layer>): IGameObject {
    init {
        // 바닥은 한 장짜리 배경이 아니라, 여러 종류의 플랫폼 타일을 흩뿌려 두는 식으로 구성한다.
        // Floor 는 클래스 내부의 고정 속도(SPEED)로 왼쪽으로 자동 스크롤한다.
        // 배치할 때는 중앙점이 아니라 왼쪽 위 모서리를 기준으로 두면,
        // 타일의 크기를 따로 계산하지 않고도 맵의 시작 위치를 바로 잡을 수 있다.
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

        // 아이템도 마찬가지로 여러 종류를 흩뿌려 둔다.
        world.add(JellyItem(gctx, 40).apply {
            setLeftTop(700f, 600f)
        }, Layer.ITEM)
        world.add(JellyItem(gctx, 29).apply {
            setLeftTop(1200f, 400f)
        }, Layer.ITEM)
    }
    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
    }
}
