package kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main

import android.graphics.PointF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.bg.TiledBackground
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy.Fly
import kotlin.math.hypot

class MainScene(gctx: GameContext): Scene(gctx) {
    override val clipsRect: Boolean = true
    enum class Layer {
        BG,
        ENEMY,
    }

    override val world = World(Layer.entries.toTypedArray())
    private val background = TiledBackground(gctx, "map/desert.tmj", tileWidth = 100f, tileHeight = 100f)
    private var tileSize = MIN_TILE_SIZE
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var lastSpan = 0f
    private val touchPoint0 = PointF()
    private val touchPoint1 = PointF()
    private var testFliesAdded = false

    init {
        // GameActivity 에서 기준 좌표계를 3200x1800 으로 잡았고,
        // desert.tmj 는 32x18 tile map 이므로 tile 하나를 100x100 으로 그리면 화면을 정확히 채운다.
        world.add(background, Layer.BG)
    }

    override fun onEnter() {
        if (testFliesAdded) return
        testFliesAdded = true
        addTestFlies()
    }

    private fun addTestFlies() {
        // Fly sprite sheet 가 type 별 frame rect 로 제대로 나뉘는지 확인하기 위한 임시 배치이다.
        // 이후 WaveGen 이 생기면 이 코드는 enemy 생성 로직으로 대체된다.
        val types = Fly.Type.entries
        for (i in types.indices) {
            val fly = Fly.get(gctx, types[i])
            fly.setCenter(500f + i * 350f, 500f)
            world.add(fly, Layer.ENEMY)
        }
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
        background.scrollBy(-dx, -dy)
    }

    private fun pinchZoom(event: MotionEvent) {
        val span = span(event)
        if (lastSpan <= 0f) return

        val focusX = focusX(event)
        val focusY = focusY(event)
        tileSize = (tileSize * span / lastSpan).coerceIn(MIN_TILE_SIZE, MAX_TILE_SIZE)
        background.setUniformTileSize(tileSize, focusX, focusY)
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
        private const val MIN_TILE_SIZE = 100f
        private const val MAX_TILE_SIZE = MIN_TILE_SIZE * 3f
    }
}
