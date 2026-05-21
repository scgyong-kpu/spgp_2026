package kr.ac.tukorea.ge.spgp2026.tudefence.game.map

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import android.util.Log
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random

object PathFinder {
    private const val TAG = "PathFinder"
    private const val PATH_TILE = 30
    private const val START_TILE = 31
    private const val END_TILE = 46
    private const val STEP_INTERVAL = 0.01f
    private const val STRAIGHT_COST = 10
    private const val DIAGONAL_COST = 14

    private var layer: TiledLayer? = null
    private var tileSize = 0f
    private var start: Point? = null
    private var end: Point? = null
    private var walkableCount = 0
    private val tileRect = RectF()
    private val infoRect = RectF()
    private val costBarRect = RectF()
    private var nodes: Array<Node?> = emptyArray()
    private val openNodes = ArrayList<Node>()
    private val rawPath = ArrayList<Node>()
    private val simplifiedPath = ArrayList<Node>()
    private val waypoints = ArrayList<Waypoint>()
    private var currentNode: Node? = null
    private var elapsedTime = 0f
    private var searchFinished = false
    private var searchFailed = false
    private var selectedTileX = INVALID_TILE
    private var selectedTileY = INVALID_TILE

    fun setTiledLayer(layer: TiledLayer, tileSize: Float) {
        this.layer = layer
        this.tileSize = tileSize
        scanMarkerLayer(layer)
        resetSearch(layer)
        Log.d(TAG, "scan: start=$start, end=$end, walkable=$walkableCount, tileSize=$tileSize")
    }

    fun update(frameTime: Float) {
        if (searchFinished || searchFailed) return
        // 선택된 tile 의 g/h/f 값을 설명하는 동안은 A* step 을 잠시 멈춘다.
        if (selectedTileX != INVALID_TILE) return

        elapsedTime += frameTime
        if (elapsedTime < STEP_INTERVAL) return

        // 수업에서 탐색 과정을 눈으로 확인하기 위해 밀린 시간만큼 while 로 따라잡지 않는다.
        elapsedTime = 0f
        step()
    }

    fun draw(canvas: Canvas) {
        val layer = layer ?: return
        drawMarkerTiles(canvas, layer)
        drawSearchTiles(canvas)
        drawRawPath(canvas)
        drawSimplifiedPath(canvas)
        drawWaypoints(canvas)
        drawStartEndTiles(canvas)
        drawSelectedTileInfo(canvas, layer)
    }

    fun selectTile(mapX: Float, mapY: Float): Boolean {
        val layer = layer ?: return false
        // Float.toInt() 는 0 쪽으로 버림하므로, 음수 좌표가 0번 tile 로 잘못 들어갈 수 있다.
        // tile index 로 바꿀 때에는 수학적인 floor 를 사용해야 map 밖 터치를 제대로 걸러낼 수 있다.
        val x = floor(mapX / tileSize).toInt()
        val y = floor(mapY / tileSize).toInt()
        if (x !in 0 until layer.width || y !in 0 until layer.height) {
            clearSelectedTile()
            return false
        }

        if (selectedTileX == x && selectedTileY == y) {
            clearSelectedTile()
        } else {
            selectedTileX = x
            selectedTileY = y
        }
        return true
    }

    private fun scanMarkerLayer(layer: TiledLayer) {
        val startIndex = layer.data.indexOf(START_TILE)
        val endIndex = layer.data.indexOf(END_TILE)
        check(startIndex >= 0) { "Marker layer must contain start tile gid=$START_TILE" }
        check(endIndex >= 0) { "Marker layer must contain end tile gid=$END_TILE" }

        start = Point(startIndex % layer.width, startIndex / layer.width)
        end = Point(endIndex % layer.width, endIndex / layer.width)
        walkableCount = layer.data.count { isWalkable(it) }
    }

    private fun resetSearch(layer: TiledLayer) {
        nodes = arrayOfNulls(layer.width * layer.height)
        openNodes.clear()
        rawPath.clear()
        simplifiedPath.clear()
        waypoints.clear()
        currentNode = null
        elapsedTime = 0f
        searchFinished = false
        searchFailed = false

        var y = 0
        while (y < layer.height) {
            var x = 0
            while (x < layer.width) {
                if (isWalkable(layer.tileAt(x, y))) {
                    nodes[indexOf(layer, x, y)] = Node(x, y)
                }
                x++
            }
            y++
        }

        val start = start ?: return
        val end = end ?: return
        val startNode = nodeAt(layer, start.x, start.y) ?: return
        startNode.g = 0
        startNode.h = heuristic(start.x, start.y, end.x, end.y)
        startNode.opened = true
        openNodes.add(startNode)
    }

    private fun step() {
        val layer = layer ?: return
        val end = end ?: return

        if (openNodes.isEmpty()) {
            searchFailed = true
            Log.d(TAG, "A* failed: path not found")
            return
        }

        val current = popBestOpenNode()
        current.closed = true
        currentNode = current

        if (current.x == end.x && current.y == end.y) {
            searchFinished = true
            buildRawPath(current)
            buildSimplifiedPath()
            buildRandomizedWaypoints(layer)
            Log.d(
                TAG,
                "A* finished: g=${current.g}, rawPath=${rawPath.size}, " +
                    "simplifiedPath=${simplifiedPath.size}, waypoints=${waypoints.size}",
            )
            return
        }

        // 이번 단계에서는 도착 tile 이 walkable 이면 대각 이동을 허용한다.
        // 모서리를 비집고 지나가는 diagonal corner-cut 검사는 필요해지면 별도 단계로 다룬다.
        var dir = 0
        while (dir < DX.size) {
            val nx = current.x + DX[dir]
            val ny = current.y + DY[dir]
            val neighbor = nodeAt(layer, nx, ny)
            if (neighbor != null && !neighbor.closed) {
                visitNeighbor(current, neighbor, MOVE_COST[dir], end)
            }
            dir++
        }
    }

    fun createRandomizedPath(toPath: Path): Boolean {
        val layer = layer ?: return false
        if (simplifiedPath.isEmpty()) return false

        // simplifiedPath 는 marker layer 가 바뀌지 않는 한 모든 Fly 가 공유할 수 있는 결과이다.
        // 하지만 waypoint 의 tile 내부 offset 은 Fly 마다 달라야 하므로,
        // 재활용된 Fly 가 init() 될 때마다 새 offset 으로 Path 를 다시 채운다.
        buildRandomizedWaypoints(layer)
        buildPathFromWaypoints(toPath)
        return true
    }

    private fun popBestOpenNode(): Node {
        var bestIndex = 0
        var bestNode = openNodes[0]

        var i = 1
        while (i < openNodes.size) {
            val node = openNodes[i]
            if (node.f < bestNode.f || node.f == bestNode.f && node.h < bestNode.h) {
                bestIndex = i
                bestNode = node
            }
            i++
        }

        openNodes.removeAt(bestIndex)
        return bestNode
    }

    private fun visitNeighbor(current: Node, neighbor: Node, moveCost: Int, end: Point) {
        val tentativeG = current.g + moveCost
        if (neighbor.opened && tentativeG >= neighbor.g) return

        neighbor.parent = current
        neighbor.g = tentativeG
        neighbor.h = heuristic(neighbor.x, neighbor.y, end.x, end.y)

        if (!neighbor.opened) {
            neighbor.opened = true
            openNodes.add(neighbor)
        }
    }

    private fun buildRawPath(endNode: Node) {
        rawPath.clear()
        var node: Node? = endNode
        while (node != null) {
            rawPath.add(node)
            node = node.parent
        }
        // parent 를 따라가면 end -> start 순서가 되므로, 이후 단계에서 쓰기 쉽게 start -> end 로 뒤집어 둔다.
        rawPath.reverse()
    }

    private fun buildSimplifiedPath() {
        simplifiedPath.clear()
        if (rawPath.isEmpty()) return

        simplifiedPath.add(rawPath.first())
        var i = 1
        while (i < rawPath.size - 1) {
            val previous = rawPath[i - 1]
            val current = rawPath[i]
            val next = rawPath[i + 1]
            val dx1 = current.x - previous.x
            val dy1 = current.y - previous.y
            val dx2 = next.x - current.x
            val dy2 = next.y - current.y
            if (dx1 != dx2 || dy1 != dy2) {
                simplifiedPath.add(current)
            }
            i++
        }
        if (rawPath.size > 1) {
            simplifiedPath.add(rawPath.last())
        }
    }

    private fun buildRandomizedWaypoints(layer: TiledLayer) {
        waypoints.clear()
        if (simplifiedPath.isEmpty()) return

        // tile 좌표 (x, y) 자체는 tile 의 좌상단이다.
        // 각 waypoint 는 tile 안쪽에서만 움직이도록 +0.2~+0.8 범위의 offset 을 더한다.
        // 0.0~1.0 전체를 쓰면 tile 경계에 너무 붙어 벽에 닿는 것처럼 보일 수 있다.
        var i = 0
        while (i < simplifiedPath.size) {
            val node = simplifiedPath[i]
            waypoints.add(
                Waypoint(
                    node.x + randomOffset(),
                    node.y + randomOffset(),
                )
            )
            i++
        }

        val firstY = waypoints.first().y
        val lastY = waypoints.last().y
        waypoints.add(0, Waypoint(-0.5f, firstY))
        waypoints.add(Waypoint(layer.width + 0.5f, lastY))
    }

    private fun randomOffset(): Float {
        return MIN_WAYPOINT_OFFSET + Random.nextFloat() * (MAX_WAYPOINT_OFFSET - MIN_WAYPOINT_OFFSET)
    }

    private fun buildPathFromWaypoints(path: Path) {
        if (waypoints.isEmpty()) return

        path.reset()
        val first = waypoints.first()
        path.moveTo(first.x * tileSize, first.y * tileSize)

        var i = 1
        while (i < waypoints.size) {
            val point = waypoints[i]
            path.lineTo(point.x * tileSize, point.y * tileSize)
            i++
        }
    }

    private fun isWalkable(gid: Int): Boolean {
        return gid == PATH_TILE || gid == START_TILE || gid == END_TILE
    }

    private fun heuristic(x1: Int, y1: Int, x2: Int, y2: Int): Int {
        val dx = abs(x1 - x2)
        val dy = abs(y1 - y2)
        val diagonal = min(dx, dy)
        val straight = dx + dy - 2 * diagonal
        return DIAGONAL_COST * diagonal + STRAIGHT_COST * straight
    }

    private fun nodeAt(layer: TiledLayer, x: Int, y: Int): Node? {
        if (x !in 0 until layer.width) return null
        if (y !in 0 until layer.height) return null
        return nodes[indexOf(layer, x, y)]
    }

    private fun indexOf(layer: TiledLayer, x: Int, y: Int): Int {
        return y * layer.width + x
    }

    private fun drawMarkerTiles(canvas: Canvas, layer: TiledLayer) {
        var y = 0
        while (y < layer.height) {
            var x = 0
            while (x < layer.width) {
                val gid = layer.tileAt(x, y)
                val paint = when (gid) {
                    PATH_TILE -> walkablePaint
                    START_TILE -> startPaint
                    END_TILE -> endPaint
                    else -> null
                }
                if (paint != null) {
                    tileRect.set(
                        x * tileSize,
                        y * tileSize,
                        (x + 1) * tileSize,
                        (y + 1) * tileSize,
                    )
                    canvas.drawRect(tileRect, paint)
                }
                x++
            }
            y++
        }
    }

    private fun drawSearchTiles(canvas: Canvas) {
        val minOpenF = minOpenF()
        val maxOpenF = maxOpenF()
        var i = 0
        while (i < nodes.size) {
            val node = nodes[i]
            if (node != null) {
                val paint = when {
                    node === currentNode -> currentPaint
                    node.closed -> closedPaint
                    node.opened -> openPaint
                    else -> null
                }
                if (paint != null) {
                    drawTile(canvas, node.x, node.y, paint)
                    drawParentArrowForClosedNode(canvas, node)
                    drawCostBarForOpenNode(canvas, node, minOpenF, maxOpenF)
                    drawCostsForOpenNode(canvas, node)
                }
            }
            i++
        }
    }

    private fun drawStartEndTiles(canvas: Canvas) {
        start?.let { drawTile(canvas, it.x, it.y, startPaint) }
        end?.let { drawTile(canvas, it.x, it.y, endPaint) }
    }

    private fun drawRawPath(canvas: Canvas) {
        var i = 0
        while (i < rawPath.size) {
            val node = rawPath[i]
            drawTile(canvas, node.x, node.y, rawPathPaint)
            i++
        }
    }

    private fun drawSimplifiedPath(canvas: Canvas) {
        var i = 0
        while (i < simplifiedPath.size) {
            val node = simplifiedPath[i]
            drawTile(canvas, node.x, node.y, simplifiedPathPaint)
            i++
        }
    }

    private fun drawWaypoints(canvas: Canvas) {
        if (waypoints.isEmpty()) return

        var i = 0
        while (i < waypoints.size) {
            val point = waypoints[i]
            val x = point.x * tileSize
            val y = point.y * tileSize
            if (i > 0) {
                val previous = waypoints[i - 1]
                canvas.drawLine(previous.x * tileSize, previous.y * tileSize, x, y, waypointLinePaint)
            }
            canvas.drawCircle(x, y, WAYPOINT_RADIUS, waypointPointPaint)
            i++
        }
    }

    private fun drawSelectedTileInfo(canvas: Canvas, layer: TiledLayer) {
        if (selectedTileX == INVALID_TILE) return

        drawTile(canvas, selectedTileX, selectedTileY, selectedPaint)
        val node = nodeAt(layer, selectedTileX, selectedTileY)
        val gid = layer.tileAt(selectedTileX, selectedTileY)
        val state = when {
            node == null -> "blocked"
            node === currentNode -> "current"
            node.closed -> "closed"
            node.opened -> "open"
            else -> "walkable"
        }

        val left = selectedTileX * tileSize
        val top = selectedTileY * tileSize
        infoRect.set(
            left + tileSize * 0.7f,
            top - tileSize * 0.1f,
            left + tileSize * 4.0f,
            top + tileSize * 2.2f,
        )
        canvas.drawRoundRect(infoRect, 8f, 8f, infoBackgroundPaint)

        val textLeft = infoRect.left + 10f
        var textY = infoRect.top + 24f
        drawInfoLine(canvas, "tile=($selectedTileX,$selectedTileY) gid=$gid", textLeft, textY)
        textY += 22f
        drawInfoLine(canvas, "state=$state", textLeft, textY)
        textY += 22f
        drawInfoLine(
            canvas,
            "g=${node?.gText() ?: "-"} h=${node?.hText() ?: "-"} f=${node?.fText() ?: "-"}",
            textLeft,
            textY,
        )
        textY += 22f
        drawInfoLine(canvas, "parent=${node?.parentText() ?: "-"}", textLeft, textY)
    }

    private fun drawInfoLine(canvas: Canvas, text: String, x: Float, y: Float) {
        canvas.drawText(text, x, y, infoTextPaint)
    }

    private fun drawParentArrowForClosedNode(canvas: Canvas, node: Node) {
        val parent = node.parent ?: return
        if (!node.closed) return

        // closed node 는 이미 A* 가 확정적으로 꺼내 본 node 이다.
        // 화살표는 이 node 가 어느 parent 에서 왔는지 보여 주기 위해 parent 방향을 가리킨다.
        val centerX = (node.x + 0.5f) * tileSize
        val centerY = (node.y + 0.5f) * tileSize
        val dx = parent.x - node.x
        val dy = parent.y - node.y
        val length = if (dx != 0 && dy != 0) DIAGONAL_ARROW_LENGTH else STRAIGHT_ARROW_LENGTH
        val directionScale = length / if (dx != 0 && dy != 0) DIAGONAL_COST else STRAIGHT_COST
        val tipX = centerX + dx * directionScale * STRAIGHT_COST
        val tipY = centerY + dy * directionScale * STRAIGHT_COST

        canvas.drawLine(centerX, centerY, tipX, tipY, parentArrowPaint)
        canvas.drawCircle(tipX, tipY, tileSize * 0.06f, parentArrowPaint)
    }

    private fun minOpenF(): Int {
        var minF = Int.MAX_VALUE
        var i = 0
        while (i < openNodes.size) {
            val node = openNodes[i]
            if (node.f < minF) {
                minF = node.f
            }
            i++
        }
        return minF
    }

    private fun maxOpenF(): Int {
        var maxF = 0
        var i = 0
        while (i < openNodes.size) {
            val node = openNodes[i]
            if (node.f > maxF) {
                maxF = node.f
            }
            i++
        }
        return maxF
    }

    private fun drawCostBarForOpenNode(canvas: Canvas, node: Node, minOpenF: Int, maxOpenF: Int) {
        if (!node.opened || node.closed || node.g == Int.MAX_VALUE || node.f <= 0 || maxOpenF <= 0) return

        // A* 는 f = g + h 가 가장 작은 후보를 먼저 고른다.
        // 그래서 막대는 아직 선택 후보인 open node 에만 그린다.
        // open set 안에서 가장 작은 f 는 10%, 가장 큰 f 는 80% 길이로 정규화해
        // "짧은 막대가 다음에 선택될 가능성이 높다"는 점을 강조한다.
        val left = node.x * tileSize + tileSize * 0.78f
        val top = node.y * tileSize + tileSize * 0.15f
        val right = node.x * tileSize + tileSize * 0.92f
        val bottom = node.y * tileSize + tileSize * 0.85f
        val fullHeight = bottom - top
        val ratio = if (minOpenF == maxOpenF) {
            MIN_OPEN_BAR_RATIO
        } else {
            MIN_OPEN_BAR_RATIO +
                (MAX_OPEN_BAR_RATIO - MIN_OPEN_BAR_RATIO) * (node.f - minOpenF) / (maxOpenF - minOpenF)
        }
        val barHeight = fullHeight * ratio
        val barTop = bottom - barHeight
        val gHeight = barHeight * node.g / node.f
        val splitY = bottom - gHeight

        costBarRect.set(left, barTop, right, bottom)
        canvas.drawRect(costBarRect, costBarBackgroundPaint)
        costBarRect.set(left, splitY, right, bottom)
        canvas.drawRect(costBarRect, gCostPaint)
        costBarRect.set(left, barTop, right, splitY)
        canvas.drawRect(costBarRect, hCostPaint)
    }

    private fun drawCostsForOpenNode(canvas: Canvas, node: Node) {
        if (!node.opened || node.closed || node.g == Int.MAX_VALUE) return

        costTextPaint.textSize = tileSize / 5f
        val left = node.x * tileSize
        val top = node.y * tileSize
        val right = left + tileSize
        val bottom = top + tileSize
        val padding = tileSize * 0.06f

        costTextPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(node.g.toString(), left + padding, top + costTextPaint.textSize, costTextPaint)
        canvas.drawText(node.f.toString(), left + padding, bottom - padding, costTextPaint)

        costTextPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(node.h.toString(), right - padding, top + costTextPaint.textSize, costTextPaint)
    }

    private fun drawTile(canvas: Canvas, x: Int, y: Int, paint: Paint) {
        tileRect.set(
            x * tileSize,
            y * tileSize,
            (x + 1) * tileSize,
            (y + 1) * tileSize,
        )
        canvas.drawRect(tileRect, paint)
    }

    private class Node(val x: Int, val y: Int) {
        var g = Int.MAX_VALUE
        var h = 0
        val f: Int
            get() = g + h
        var parent: Node? = null
        var opened = false
        var closed = false

        fun gText(): String = if (g == Int.MAX_VALUE) "-" else g.toString()
        fun hText(): String = if (g == Int.MAX_VALUE) "-" else h.toString()
        fun fText(): String = if (g == Int.MAX_VALUE) "-" else f.toString()
        fun parentText(): String = parent?.let { "(${it.x},${it.y})" } ?: "-"
    }

    private class Waypoint(val x: Float, val y: Float)

    private fun clearSelectedTile() {
        selectedTileX = INVALID_TILE
        selectedTileY = INVALID_TILE
    }

    private val walkablePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x407F7FFF // semi-transparent violet
    }
    private val openPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x6000A0FF // semi-transparent sky blue
    }
    private val closedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x606060C0 // semi-transparent blue gray
    }
    private val currentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xC0FFFF00.toInt() // semi-transparent yellow
    }
    private val parentArrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 4f
        color = 0xFFFFFFFF.toInt() // white
    }
    private val rawPathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x80FF00FF.toInt() // semi-transparent magenta
    }
    private val simplifiedPathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = 0xFF00FFFF.toInt() // cyan
    }
    private val waypointLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = 0xFF40FF40.toInt() // bright green
    }
    private val waypointPointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xFF40FF40.toInt() // bright green
    }
    private val selectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = 0xFFFFFFFF.toInt() // white
    }
    private val infoBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xCC000000.toInt() // semi-transparent black
    }
    private val infoTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 18f
        color = 0xFFFFFFFF.toInt() // white
    }
    private val costBarBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = 0xCCFFFFFF.toInt() // semi-transparent white
    }
    private val gCostPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xCCFF4040.toInt() // semi-transparent red
    }
    private val hCostPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xCCFFD040.toInt() // semi-transparent yellow orange
    }
    private val costTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xFFFFFFFF.toInt() // white
    }
    private val startPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x8000FF00.toInt() // semi-transparent green
    }
    private val endPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x80FF0000.toInt() // semi-transparent red
    }

    private val DX = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
    private val DY = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    private val MOVE_COST = intArrayOf(
        DIAGONAL_COST, STRAIGHT_COST, DIAGONAL_COST,
        STRAIGHT_COST, STRAIGHT_COST,
        DIAGONAL_COST, STRAIGHT_COST, DIAGONAL_COST,
    )
    private const val MIN_OPEN_BAR_RATIO = 0.1f
    private const val MAX_OPEN_BAR_RATIO = 0.8f
    private const val STRAIGHT_ARROW_LENGTH = 18f
    private const val DIAGONAL_ARROW_LENGTH = 25f
    private const val WAYPOINT_RADIUS = 8f
    private const val MIN_WAYPOINT_OFFSET = 0.2f
    private const val MAX_WAYPOINT_OFFSET = 0.8f
    private const val INVALID_TILE = -1
}
