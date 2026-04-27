package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.game.main.MainScene.Layer

class MapLoader(val gctx: GameContext): IGameObject {
    // kotlin.UninitializedPropertyAccessException: lateinit property scene has not been initialized
    val world = (gctx.scene as MainScene).world
    init {
        // 바닥은 한 장짜리 배경이 아니라, 여러 종류의 플랫폼 타일을 흩뿌려 두는 식으로 구성한다.
        // Floor 는 클래스 내부의 고정 속도(SPEED)로 왼쪽으로 자동 스크롤한다.
        world.add(Floor(gctx, Floor.Type.T_10x2).apply {
            setCenter(500f, 800f)
        }, Layer.FLOOR)
        world.add(Floor(gctx, Floor.Type.T_2x2).apply {
            setCenter(1100f, 800f)
        }, Layer.FLOOR)
        world.add(Floor(gctx, Floor.Type.T_3x1).apply {
            setCenter(500f, 300f)
        }, Layer.FLOOR)
        world.add(Floor(gctx, Floor.Type.T_3x1).apply {
            setCenter(1000f, 500f)
        }, Layer.FLOOR)

        // 아이템도 마찬가지로 여러 종류를 흩뿌려 둔다.
        world.add(JellyItem(gctx, 40).apply {
            setCenter(700f, 600f)
        }, Layer.ITEM)
        world.add(JellyItem(gctx, 29).apply {
            setCenter(1200f, 400f)
        }, Layer.ITEM)
    }
    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
    }
}