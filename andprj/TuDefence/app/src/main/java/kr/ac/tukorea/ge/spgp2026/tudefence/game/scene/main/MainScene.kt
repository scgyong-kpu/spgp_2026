package kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main

import android.graphics.PointF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.MapCamera
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.bg.TiledBackground
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller.WaveGen
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.weapon.Cannon
import kotlin.math.hypot

class MainScene(gctx: GameContext): Scene(gctx) {
    override val clipsRect: Boolean = true
    enum class Layer {
        BG,
        WEAPON,
        ENEMY,
        SHELL,
        CONTROLLER,
    }

    private val mapCamera = MapCamera(gctx, MAP_WIDTH, MAP_HEIGHT)
    override val world = MainWorld(mapCamera)
    private val background = TiledBackground(gctx, "map/desert.tmj", mapCamera, tileWidth = 50f, tileHeight = 50f)
    private var cameraScale = MIN_CAMERA_SCALE
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var lastSpan = 0f
    private val touchPoint0 = PointF()
    private val touchPoint1 = PointF()

    init {
        // GameActivity 에서 기준 좌표계를 1600x900 으로 잡았고,
        // desert.tmj 는 32x18 tile map 이므로 tile 하나를 50x50 으로 그리면 화면을 정확히 채운다.
        world.add(background, Layer.BG)
        world.add(WaveGen(gctx, world), Layer.CONTROLLER)

        addTestCannons(gctx)
    }

    private fun addTestCannons(gctx: GameContext) {
        // Fly path 를 눈으로 피하고, 벽돌 tile 위에 올라가도록 고른 임시 배치이다.
        // 지금은 Cannon 의 body/barrel 분리 표시와 camera transform 동작을 확인하기 위한 테스트용이다.
        addCannon(gctx, 325f, 275f, level = 1)
        addCannon(gctx, 425f, 375f, level = 2)
        addCannon(gctx, 800f, 325f, level = 3)
        addCannon(gctx, 900f, 575f, level = 4)
        addCannon(gctx, 1225f, 275f, level = 5)
        addCannon(gctx, 1275f, 800f, level = 6)
    }

    private fun addCannon(gctx: GameContext, x: Float, y: Float, level: Int) {
        world.add(Cannon.get(gctx, level).apply { setCenter(x, y) }, Layer.WEAPON)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN,
            -> {
                saveTouchState(event)
                return true
            }

            MotionEvent.ACTION_POINTER_UP -> {
                saveTouchStateAfterPointerUp(event)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount >= 2) {
                    pinchZoom(event)
                } else {
                    dragScroll(event)
                }
                saveTouchState(event)
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL,
            -> {
                lastSpan = 0f
                return true
            }
        }
        return true
    }

    private fun dragScroll(event: MotionEvent) {
        pointFromEvent(event, 0, touchPoint0)
        val dx = touchPoint0.x - lastTouchX
        val dy = touchPoint0.y - lastTouchY
        mapCamera.scrollBy(-dx, -dy)
    }

    private fun pinchZoom(event: MotionEvent) {
        val span = span(event)
        if (lastSpan <= 0f) return

        val focusX = focusX(event)
        val focusY = focusY(event)
        cameraScale = (cameraScale * span / lastSpan).coerceIn(MIN_CAMERA_SCALE, MAX_CAMERA_SCALE)
        mapCamera.setScale(cameraScale, focusX, focusY)
    }

    private fun saveTouchState(event: MotionEvent) {
        lastTouchX = focusX(event)
        lastTouchY = focusY(event)
        lastSpan = if (event.pointerCount >= 2) span(event) else 0f
    }

    private fun saveTouchStateAfterPointerUp(event: MotionEvent) {
        lastSpan = 0f
        if (event.pointerCount <= 1) return

        // ACTION_POINTER_UP 이벤트에서는 아직 올라간 pointer 도 event 안에 남아 있다.
        // 그대로 평균을 내면 다음 drag 시작점이 살짝 튈 수 있으므로, 올라간 pointer 를 제외한 하나를 기준으로 잡는다.
        val remainingIndex = if (event.actionIndex == 0) 1 else 0
        pointFromEvent(event, remainingIndex, touchPoint0)
        lastTouchX = touchPoint0.x
        lastTouchY = touchPoint0.y
    }

    private fun focusX(event: MotionEvent): Float {
        var sum = 0f
        for (i in 0 until event.pointerCount) {
            pointFromEvent(event, i, touchPoint0)
            sum += touchPoint0.x
        }
        return sum / event.pointerCount
    }

    private fun focusY(event: MotionEvent): Float {
        var sum = 0f
        for (i in 0 until event.pointerCount) {
            pointFromEvent(event, i, touchPoint0)
            sum += touchPoint0.y
        }
        return sum / event.pointerCount
    }

    private fun span(event: MotionEvent): Float {
        pointFromEvent(event, 0, touchPoint0)
        pointFromEvent(event, 1, touchPoint1)
        val dx = touchPoint0.x - touchPoint1.x
        val dy = touchPoint0.y - touchPoint1.y
        return hypot(dx, dy)
    }

    private fun pointFromEvent(event: MotionEvent, pointerIndex: Int, out: PointF) {
        gctx.metrics.fromScreen(event.getX(pointerIndex), event.getY(pointerIndex), out)
    }

    companion object {
        const val MAP_WIDTH = 1600f
        const val MAP_HEIGHT = 900f
        private const val MIN_CAMERA_SCALE = 1f
        private const val MAX_CAMERA_SCALE = 3f
    }
}
