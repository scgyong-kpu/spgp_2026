package kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main

import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.MapCamera
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.PathFinder
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledMapLoader
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.bg.TiledBackground
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller.CannonMenu
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller.CollisionChecker
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller.Selection
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller.WaveGen
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.hud.Score
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.weapon.Cannon
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.pause.PauseScene
import kotlin.math.hypot

class MainScene(gctx: GameContext, private val stage: Int): Scene(gctx), CannonMenu.OnMenuListener {
    override val clipsRect: Boolean = true

    private val mapAssetPath = mapAssetPathFor(stage)
    private val tiledMap = TiledMapLoader.load(gctx.view.context.assets, mapAssetPath)
    private val mapCamera = MapCamera(
        gctx,
        tiledMap.width * TILE_WIDTH,
        tiledMap.height * TILE_HEIGHT,
    )
    override val world = MainWorld(mapCamera)
    private val background = TiledBackground(
        gctx,
        mapAssetPath,
        tiledMap,
        mapCamera,
        tileWidth = TILE_WIDTH,
        tileHeight = TILE_HEIGHT,
    )
    private val markerLayer = tiledMap.tileLayer(MARKER_LAYER_NAME)
    private val score = Score(gctx)
    private val selection = Selection(gctx, Cannon.SIZE, Cannon.SIZE)
    private val cannonMenu = CannonMenu(gctx)
    private var cameraScale = MIN_CAMERA_SCALE
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var lastSpan = 0f
    private var downTouchX = 0f
    private var downTouchY = 0f
    private var downTime = 0L
    private var isDragging = false
    private var wasMultiTouch = false
    private var menuGestureConsumed = false
    private val touchPoint0 = PointF()
    private val touchPoint1 = PointF()
    private val mapPoint = PointF()
    private val selectionSceneRect = RectF()

    init {
        // GameActivity 에서 기준 좌표계를 1600x900 으로 잡았고,
        // stage map 은 32x18 tile map 이므로 tile 하나를 50x50 으로 그리면 화면을 정확히 채운다.
        PathFinder.setTiledLayer(markerLayer, TILE_WIDTH)
        cannonMenu.onMenuListener = this
        world.add(background, MainLayer.BG)
        world.add(score, MainLayer.UI)
        world.add(selection, MainLayer.SELECTOR)
        world.add(cannonMenu, MainLayer.UI)
        world.add(WaveGen(gctx, world), MainLayer.CONTROLLER)
        score.setScore(30)

        world.add(CollisionChecker(gctx, world, score), MainLayer.CONTROLLER)
    }

    private fun addCannon(gctx: GameContext, x: Float, y: Float, level: Int) {
        world.add(Cannon.get(gctx, level).apply { setCenter(x, y) }, MainLayer.WEAPON)
    }

    override fun onBackPressed(): Boolean {
        gctx.sceneStack.push(PauseScene(gctx))
        return true
    }

    /*
     * Touch policy summary
     *
     * | Situation | Selection | Menu | Note |
     * |---|---|---|---|
     * | DOWN / MOVE | show | hidden or same | tile 의 가능/불가 상태를 즉시 보여 준다 |
     * | UP on installable tile | keep | install menu | cannon 이 선택돼 있으면 manage menu |
     * | UP on non-installable tile | hide | hide | selection 이 사라진다 |
     * | menu item success (install) | keep | switch to manage menu | install 실패 시 그대로 유지 |
     * | menu item success (upgrade) | keep | keep manage menu | upgrade 실패 시 그대로 유지 |
     * | menu item success (uninstall) | hide | hide | both disappear |
     * | menu visible + outside DOWN / MOVE | show again | hide | menu 를 닫고 바로 selection 을 다시 보여 준다 |
     * | first MOVE within drag window | drag | hide or keep | drag 가능하면 map drag, 아니면 selection update |
     * | multi-touch start / move | hide | hide | pinch 우선 |
     *
     * 이 표는 selection/menu 상태 전이를 한 곳에서 읽기 쉽게 정리한 것이다.
     * 실제 구현은 ACTION_DOWN / MOVE / UP / POINTER_DOWN 분기에서 이 규칙을 따른다.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            -> {
                wasMultiTouch = false
                isDragging = false
                menuGestureConsumed = false
                downTime = event.eventTime
                saveTouchState(event)
                pointFromEvent(event, 0, touchPoint0)
                mapCamera.gameToMap(touchPoint0.x, touchPoint0.y, mapPoint)
                // PathFinder 학습 단계에서는 tile 상태를 터치로 확인하는 것이 우선이므로,
                // map 안쪽 터치는 cannon 선택/설치 로직까지 내려보내지 않는다.
                if (PathFinder.selectTile(mapPoint.x, mapPoint.y)) {
                    selection.hide()
                    cannonMenu.hide()
                    return true
                }
                if (cannonMenu.contains(touchPoint0.x, touchPoint0.y)) {
                    menuGestureConsumed = true
                    cannonMenu.onTouch(touchPoint0.x, touchPoint0.y)
                    return true
                }
                if (cannonMenu.isVisible) {
                    cannonMenu.hide()
                }
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
                cannonMenu.hide()
                saveTouchState(event)
                return true
            }

            MotionEvent.ACTION_POINTER_UP -> {
                saveTouchStateAfterPointerUp(event)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (menuGestureConsumed) {
                    return true
                }
                if (event.pointerCount >= 2) {
                    wasMultiTouch = true
                    isDragging = true
                    selection.hide()
                    cannonMenu.hide()
                    pinchZoom(event)
                } else {
                    dragScrollIfNeeded(event)
                }
                updateSelection(event)
                saveTouchState(event)
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (menuGestureConsumed) {
                    resetTouchState()
                    return true
                }
                if (!showCannonMenuIfTap(event)) {
                    selection.hide()
                }
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
            return
        }
        pointFromEvent(event, 0, touchPoint0)
        mapCamera.gameToMap(touchPoint0.x, touchPoint0.y, mapPoint)
        findCannonAt(mapPoint.x, mapPoint.y)?.let { cannon ->
            selection.selectCannon(cannon)
            return
        }
        val cx = tileCenterX(mapPoint.x)
        val cy = tileCenterY(mapPoint.y)
        selection.moveTo(cx, cy, canInstallAt(cx, cy) && !hasOverlappingCannon(cx, cy))
    }

    private fun showCannonMenuIfTap(event: MotionEvent): Boolean {
        if (wasMultiTouch || isDragging) {
            cannonMenu.hide()
            return false
        }

        if (selection.selectedCannon != null) {
            selection.sceneRect(mapCamera, selectionSceneRect)
            cannonMenu.showManageMenuAt(selectionSceneRect, selection.selectedCannon!!.level)
            return true
        }

        pointFromEvent(event, 0, touchPoint0)
        mapCamera.gameToMap(touchPoint0.x, touchPoint0.y, mapPoint)
        val cannonX = tileCenterX(mapPoint.x)
        val cannonY = tileCenterY(mapPoint.y)
        if (!canInstallAt(cannonX, cannonY)) {
            cannonMenu.hide()
            return false
        }
        if (hasOverlappingCannon(cannonX, cannonY)) {
            cannonMenu.hide()
            return false
        }

        selection.sceneRect(mapCamera, selectionSceneRect)
        cannonMenu.showInstallMenuAt(selectionSceneRect)
        return true
    }

    override fun onMenuSelected(resId: Int) {
        val resName = gctx.view.context.resources.getResourceEntryName(resId)
        Log.d(javaClass.simpleName, "resId: $resName")
        val selectedCannon = selection.selectedCannon
        when (resId) {
            R.mipmap.f_01_01 -> installCannon(level = 1)
            R.mipmap.f_01_02 -> installCannon(level = 2)
            R.mipmap.f_01_03 -> installCannon(level = 3)
            R.mipmap.upgrade -> upgradeCannon(selectedCannon)
            R.mipmap.uninstall -> {
                if (selectedCannon != null) {
                    score.add(selectedCannon.sellPrice())
                }
                selectedCannon?.uninstall()
                selection.hide()
                cannonMenu.hide()
            }
        }
    }

    override fun isMenuItemProhibited(resId: Int): Boolean {
        val selectedCannon = selection.selectedCannon
        return when (resId) {
            R.mipmap.f_01_01 -> score.value < Cannon.installationCost(1)
            R.mipmap.f_01_02 -> score.value < Cannon.installationCost(2)
            R.mipmap.f_01_03 -> score.value < Cannon.installationCost(3)
            R.mipmap.upgrade -> {
                selectedCannon == null ||
                    Cannon.upgradeCost(selectedCannon.level) == Int.MAX_VALUE ||
                    score.value < Cannon.upgradeCost(selectedCannon.level)
            }
            R.mipmap.uninstall -> false
            else -> false
        }
    }

    private fun installCannon(level: Int): Boolean {
        val cost = Cannon.installationCost(level)
        if (score.value < cost) {
            return false
        }
        score.add(-cost)
        selection.mapRect(selectionSceneRect)
        val cannon = Cannon.get(gctx, level).also { cannon ->
            cannon.setCenter(selectionSceneRect.centerX(), selectionSceneRect.centerY())
            world.add(
                cannon,
                MainLayer.WEAPON,
            )
        }
        selection.selectCannon(cannon)
        selection.sceneRect(mapCamera, selectionSceneRect)
        cannonMenu.showManageMenuAt(selectionSceneRect, cannon.level)
        return true
    }

    private fun upgradeCannon(selectedCannon: Cannon?): Boolean {
        if (selectedCannon == null) return false
        val cost = Cannon.upgradeCost(selectedCannon.level)
        if (cost == Int.MAX_VALUE || score.value < cost) {
            return false
        }
        if (!selectedCannon.upgrade()) {
            return false
        }
        score.add(-cost)
        // 업그레이드 후에도 manage menu 는 유지하고,
        // 왼쪽의 레벨 숫자만 새 값으로 다시 그리도록 메뉴를 갱신한다.
        selection.sceneRect(mapCamera, selectionSceneRect)
        cannonMenu.showManageMenuAt(selectionSceneRect, selectedCannon.level)
        return true
    }

    private fun findCannonAt(mapX: Float, mapY: Float): Cannon? {
        val cannons = world.objectsAt(MainLayer.WEAPON)
        var index = 0
        while (index < cannons.size) {
            val cannon = cannons[index] as? Cannon
            if (cannon != null && containsPoint(cannon, mapX, mapY)) {
                return cannon
            }
            index++
        }
        return null
    }

    private fun containsPoint(cannon: Cannon, mapX: Float, mapY: Float): Boolean {
        return mapX >= cannon.x - cannon.width / 2f &&
            mapX <= cannon.x + cannon.width / 2f &&
            mapY >= cannon.y - cannon.height / 2f &&
            mapY <= cannon.y + cannon.height / 2f
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
            if (isDragging) {
                selection.hide()
            }
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
        private const val MARKER_LAYER_NAME = "Marker"
        private const val INSTALLABLE_TILE_GID = 10
        private const val TILE_WIDTH = 50f
        private const val TILE_HEIGHT = 50f
        private const val MIN_CAMERA_SCALE = 1f
        private const val MAX_CAMERA_SCALE = 3f
        private const val TAP_SLOP = 16f
        private const val TAP_TIMEOUT_MS = 250L

        private fun mapAssetPathFor(stage: Int): String {
            return "map/stage_${stage.coerceIn(1, 3)}.tmj"
        }
    }
}
