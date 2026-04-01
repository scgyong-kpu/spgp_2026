package kr.ac.tukorea.ge.spgp2026.dragonflight

import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class DragonFlightActivity : BaseGameActivity() {
    override fun createRootScene(gctx: GameContext): Scene {
        // 이번 단계의 목표는 DragonFlightActivity 가 BaseGameActivity 를 상속받아
        // a2dg 의 GameView / Scene 흐름으로 진입하는 것까지 확인하는 데 있다.
        //
        // 그래서 아직은 MainScene 파일을 따로 만들지 않고,
        // 가장 작은 형태인 anonymous Scene 을 root scene 으로 반환한다.
        return object : Scene(gctx) {
        }
    }
}
