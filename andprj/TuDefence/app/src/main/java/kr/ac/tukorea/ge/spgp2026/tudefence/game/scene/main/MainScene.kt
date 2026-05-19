package kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main

import android.graphics.PointF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.MapCamera
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledMapLoader
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.bg.TiledBackground
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller.CollisionChecker
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller.Selection
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller.WaveGen
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.weapon.Cannon
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.pause.PauseScene
import kotlin.math.hypot

class MainScene(gctx: GameContext): Scene(gctx) {
    override val clipsRect: Boolean = true

    private val tiledMap = TiledMapLoader.load(gctx.view.context.assets, MAP_ASSET_PATH)
    private val mapCamera = MapCamera(
        gctx,
        tiledMap.width * TILE_WIDTH,
        tiledMap.height * TILE_HEIGHT,
    )
    override val world = MainWorld(mapCamera)
    private val background = TiledBackground(
        gctx,
        MAP_ASSET_PATH,
        tiledMap,
        mapCamera,
        tileWidth = TILE_WIDTH,
        tileHeight = TILE_HEIGHT,
    )
    private val markerLayer = tiledMap.tileLayer(MARKER_LAYER_NAME)
    private val selection = Selection(gctx, Cannon.SIZE, Cannon.SIZE)
    private var cameraScale = MIN_CAMERA_SCALE
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var lastSpan = 0f
    private var downTouchX = 0f
    private var downTouchY = 0f
    private var downTime = 0L
    private var isDragging = false
    private var wasMultiTouch = false
    private val touchPoint0 = PointF()
    private val touchPoint1 = PointF()
    private val mapPoint = PointF()

    init {
        // GameActivity 에서 기준 좌표계를 1600x900 으로 잡았고,
        // desert.tmj 는 32x18 tile map 이므로 tile 하나를 50x50 으로 그리면 화면을 정확히 채운다.
        world.add(background, MainLayer.BG)
        world.add(selection, MainLayer.SELECTOR)
        world.add(WaveGen(gctx, world), MainLayer.CONTROLLER)

        world.add(CollisionChecker(gctx, world), MainLayer.CONTROLLER)
        addTestCannons(gctx)
    }

    private fun addTestCannons(gctx: GameContext) {
        // Fly path 를 눈으로 피하고, 벽돌 tile 위에 올라가도록 고른 임시 배치이다.
        // 지금은 Cannon 의 body/barrel 분리 표시와 camera transform 동작을 확인하기 위한 테스트용이다.
        addCannon(gctx, 250f, 275f, level = 1)
        addCannon(gctx, 425f, 375f, level = 2)
        addCannon(gctx, 800f, 325f, level = 6)
        addCannon(gctx, 900f, 575f, level = 4)
        addCannon(gctx, 1225f, 275f, level = 5)
        addCannon(gctx, 1275f, 800f, level = 6)
        //addCannon(gctx, 800f, 125f, level = 10)
    }

    private fun addCannon(gctx: GameContext, x: Float, y: Float, level: Int) {
        world.add(Cannon.get(gctx, level).apply { setCenter(x, y) }, MainLayer.WEAPON)
    }

    override fun onBackPressed(): Boolean {
        gctx.sceneStack.push(PauseScene(gctx))
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            -> {
                wasMultiTouch = false
                isDragging = false
                downTime = event.eventTime
                saveTouchState(event)
                downTouchX = lastTouchX
                downTouchY = lastTouchY
                updateSelection(event)
                return true
            }

            MotionEvent.ACTION_POINTER_DOWN,
            -> {
                wasMultiTouch = true
                isDragging = true
                selection.hide()
                saveTouchState(event)
                return true
            }

            MotionEvent.ACTION_POINTER_UP -> {
                saveTouchStateAfterPointerUp(event)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount >= 2) {
                    wasMultiTouch = true
                    isDragging = true
                    pinchZoom(event)
                } else {
                    dragScrollIfNeeded(event)
                }
                updateSelection(event)
                saveTouchState(event)
                return true
            }

            MotionEvent.ACTION_UP -> {
                installCannonIfTap(gctx, event)
                selection.hide()
                resetTouchState()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                selection.hide()
                resetTouchState()
                return true
            }
        }
        return true
    }

    private fun updateSelection(event: MotionEvent) {
        if (wasMultiTouch || isDragging) {
            selection.hide()
            return
        }
        pointFromEvent(event, 0, touchPoint0)
        mapCamera.gameToMap(touchPoint0.x, touchPoint0.y, mapPoint)
        val cx = tileCenterX(mapPoint.x)
        val cy = tileCenterY(mapPoint.y)
        selection.moveTo(cx, cy, canInstallAt(cx, cy))
    }

    private fun installCannonIfTap(gctx: GameContext, event: MotionEvent) {
        if (wasMultiTouch || isDragging) return

        pointFromEvent(event, 0, touchPoint0)
        mapCamera.gameToMap(touchPoint0.x, touchPoint0.y, mapPoint)
        val cannonX = tileCenterX(mapPoint.x)
        val cannonY = tileCenterY(mapPoint.y)
        if (!canInstallAt(cannonX, cannonY)) return
        if (hasOverlappingCannon(cannonX, cannonY)) return

        addCannon(gctx, cannonX, cannonY, level = 1)
    }

    private fun tileCenterX(mapX: Float): Float {
        return (mapX / TILE_WIDTH).toInt() * TILE_WIDTH + TILE_WIDTH / 2f
    }

    private fun tileCenterY(mapY: Float): Float {
        return (mapY / TILE_HEIGHT).toInt() * TILE_HEIGHT + TILE_HEIGHT / 2f
    }

    private fun canInstallAt(x: Float, y: Float): Boolean {
        // 올해 버전에서는 Cannon 이 차지하는 2x2 영역을 검사하지 않고,
        // snap 된 중심점이 속한 tile 하나만 Marker layer 에서 확인한다.
        val tileX = (x / TILE_WIDTH).toInt()
        val tileY = (y / TILE_HEIGHT).toInt()
        return markerLayer.tileAt(tileX, tileY) == INSTALLABLE_TILE_GID
    }

    private fun hasOverlappingCannon(x: Float, y: Float): Boolean {
        val cannons = world.objectsAt(MainLayer.WEAPON)
        var index = 0
        while (index < cannons.size) {
            val cannon = cannons[index] as? Cannon
            if (cannon != null && cannon.intersectsIfInstalledAt(x, y)) return true
            index++
        }
        return false
    }

    private fun resetTouchState() {
        lastSpan = 0f
        isDragging = false
        wasMultiTouch = false
    }

    private fun dragScrollIfNeeded(event: MotionEvent) {
        pointFromEvent(event, 0, touchPoint0)
        val dxFromDown = touchPoint0.x - downTouchX
        val dyFromDown = touchPoint0.y - downTouchY
        if (!isDragging) {
            val movedFar = dxFromDown * dxFromDown + dyFromDown * dyFromDown > TAP_SLOP * TAP_SLOP
            val withinDragWindow = event.eventTime - downTime < TAP_TIMEOUT_MS
            val canScroll = cameraScale > MIN_CAMERA_SCALE
            isDragging = movedFar && withinDragWindow && canScroll
        }
        if (isDragging) {
            dragScroll(event)
        }
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
        private const val MAP_ASSET_PATH = "map/desert.tmj"
        private const val MARKER_LAYER_NAME = "Marker"
        private const val INSTALLABLE_TILE_GID = 10
        private const val TILE_WIDTH = 50f
        private const val TILE_HEIGHT = 50f
        private const val MIN_CAMERA_SCALE = 1f
        private const val MAX_CAMERA_SCALE = 3f
        private const val TAP_SLOP = 16f
        private const val TAP_TIMEOUT_MS = 250L
    }
}
