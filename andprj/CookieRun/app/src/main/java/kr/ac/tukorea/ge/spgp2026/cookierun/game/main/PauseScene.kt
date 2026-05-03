package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Button
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.DrawableSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

class PauseScene(gctx: GameContext) : Scene(gctx) {
    enum class Layer {
        BG, TITLE, TOUCH
    }

    override val clipsRect = true
    override val isTransparent = true
    private val panelWidth = gctx.metrics.width / 2f
    private val panelHeight = gctx.metrics.height / 2f

    override val world = World(Layer.entries.toTypedArray()).apply {
        // PauseScene 은 transparent Scene 이므로 아래 MainScene 도 함께 그려진다.
        // 여기서는 bitmap 리소스 대신 DrawableSprite 로 반투명 검정 overlay 를 그린다.
        add(DrawableSprite(ColorDrawable(Color.argb(128, 0, 0, 0))).apply {
            setCenter(gctx.metrics.width / 2f, gctx.metrics.height / 2f)
            setSize(gctx.metrics.width, gctx.metrics.height)
        }, Layer.BG)
        // XML drawable 로 만든 rounded rectangle panel 을 DrawableSprite 로 그린다.
        // bitmap 없이도 stroke, corner radius, 반투명 fill 같은 UI 모양을 리소스 XML 로 표현할 수 있다.
        add(DrawableSprite(gctx.res.getDrawable(R.drawable.pause_panel)).apply {
            setCenter(gctx.metrics.width / 2f, gctx.metrics.height / 2f)
            setSize(panelWidth, panelHeight)
        }, Layer.BG)

        add(Sprite(gctx, R.mipmap.cookie_run_title).apply {
            setCenter(gctx.metrics.width / 2f, 360f)
            setSize(369f, 136f)
        }, Layer.TITLE)

        // Resume 버튼은 현재 PauseScene 만 pop 해서 아래 MainScene 으로 돌아간다.
        add(Button(gctx, R.mipmap.btn_resume_n, 1450f, 100f, 200f, 75f) { pressed ->
            if (pressed) {
                pop()
            }
            false
        }, Layer.TOUCH)
        add(Button(gctx, R.mipmap.btn_exit_n, gctx.metrics.width / 2f, 550f, 267f, 100f) { pressed ->
            if (pressed) {
                // 현재 stack 이 MainScene -> PauseScene 두 단계라고 알고 있으므로,
                // pop() 을 두 번 호출하면 PauseScene 과 MainScene 이 차례로 빠진다.
                // 하지만 이 코드는 "게임 종료"라는 의도가 드러나지 않고,
                // stack 구조가 바뀌면 쉽게 깨지는 임시 구현이다.
                // 다음 단계에서는 SceneStack.popAll() 같은 명시적인 함수로 바꿀 예정이다.
                pop()
                pop()
            }
            false
        }, Layer.TOUCH)
    }

    override fun touchObjects(): List<IGameObject> {
        return world.objectsAt(Layer.TOUCH)
    }

//    override fun onBackPressed(): Boolean {
//        // Scene 기본 구현도 stack 에 아래 Scene 이 있으면 pop() 하므로,
//        // PauseScene 은 이 override 없이도 Back 키로 닫힌다.
//        // 수업 중 기본 구현과 override 구현을 비교해 보이기 위해 주석으로 남겨 둔다.
//        pop()
//        return true
//    }
}
