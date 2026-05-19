package kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.tudefence.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.MapCamera

// MainWorld 는 MainScene 의 layer draw 정책을 담는 World 이다.
// 이전 단계에서는 CameraBegin / CameraEnd 객체가 서로 다른 layer 에서
// canvas.save() 와 canvas.restore() 를 나누어 호출했다.
// 그 방식은 transform 범위를 눈으로 보여주기에는 좋지만,
// save/restore 짝이 다른 객체에 흩어져 layer 순서나 remove 실수에 취약하다.
//
// 여기서는 한 함수 안에서 save -> concat -> map layer draw -> restore 를 모두 끝낸다.
// SCREEN_LAYER_START 전까지는 map camera 의 확대/이동 영향을 받고,
// 그 layer 부터는 입력/관리/UI 처럼 화면 좌표계에 그대로 남는다.
class MainWorld(
    private val mapCamera: MapCamera,
) : World<MainLayer>(MainLayer.entries.toTypedArray()) {
    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.concat(mapCamera.matrix)
        drawLayers(canvas, startOrdinal = 0, endOrdinal = SCREEN_LAYER_START.ordinal)
        canvas.restore()

        drawLayers(canvas, startOrdinal = SCREEN_LAYER_START.ordinal, endOrdinal = MainLayer.entries.size)
        drawDebugBoxes(canvas)
    }

    private fun drawLayers(canvas: Canvas, startOrdinal: Int, endOrdinal: Int) {
        // draw() 는 매 프레임 호출되는 hot path 이므로 for-each 대신 index 기반 while 을 사용한다.
        // layer 를 추가할 때는 MainLayer enum 의 순서만 정하면 된다.
        // SCREEN_LAYER_START 보다 앞이면 map 좌표계, 그 layer 부터는 screen 좌표계로 그려진다.
        var index = startOrdinal
        while (index < endOrdinal) {
            drawLayer(canvas, MainLayer.entries[index])
            index++
        }
    }

    companion object {
        // 이 layer 부터는 map camera matrix 를 적용하지 않는다.
        // 예를 들어 CONTROLLER, UI, debug overlay 는 화면 좌표계에 고정되어야 하므로
        // enum 에서 이 값 이후에 배치한다.
        private val SCREEN_LAYER_START = MainLayer.CONTROLLER
    }
}
