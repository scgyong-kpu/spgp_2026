package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.MapCamera

// 교육용 임시 구조:
// 이 객체는 실제 그림을 그리지 않고 Canvas 상태를 바꾸는 marker 역할을 한다.
// CAMERA_BEGIN layer 에 두면 이후 layer 들이 mapCamera.matrix 영향을 받는다.
// save/restore 짝이 서로 다른 객체에 나뉘므로 장기 구조로는 취약하다.
// 다음 단계에서는 Scene.draw() 의 같은 scope 안에서 withSave/concat 을 쓰는 구조로 바꾸는 편이 안전하다.
class CameraBegin(
    private val mapCamera: MapCamera,
) : IGameObject {
    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.concat(mapCamera.matrix)
    }
}
