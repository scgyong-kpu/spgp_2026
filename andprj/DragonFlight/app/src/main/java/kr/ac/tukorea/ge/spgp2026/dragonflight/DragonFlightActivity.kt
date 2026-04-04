package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class DragonFlightActivity : BaseGameActivity() {
    override val drawsDebugGrid = BuildConfig.DEBUG
    override val drawsDebugInfo = BuildConfig.DEBUG
    override val drawsFpsGraph = BuildConfig.DEBUG

    override fun createRootScene(gctx: GameContext): Scene {
        // 이제는 anonymous Scene 대신 별도 MainScene 클래스를 root scene 으로 사용한다.
        // 이렇게 하면 이후 commit 에서 Fighter, Bullet, Background 같은 요소를
        // MainScene 파일 안에 차례대로 추가해 가는 흐름이 더 분명하게 보인다.
        return MainScene(gctx)
    }
}
