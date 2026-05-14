package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// CameraBegin 이 열어 둔 Canvas save stack 을 닫는 marker 객체이다.
// begin/end 방식은 layer 순서에 의존하므로 두 객체는 remove/recycle 대상이 되면 안 된다.
// 이 커밋에서는 transform 범위를 눈으로 확인하기 위해 일부러 GameObject 로 구현한다.
class CameraEnd : IGameObject {
    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        canvas.restore()
    }
}
