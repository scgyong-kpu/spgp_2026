package kr.ac.tukorea.ge.spgp2026.tudefence.game.map

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.util.Log
import kotlin.math.abs
import kotlin.math.min

object PathFinder {
    private const val TAG = "PathFinder"
    private const val PATH_TILE = 30
    private const val START_TILE = 31
    private const val END_TILE = 46
    private const val STEP_INTERVAL = 0.05f
    private const val STRAIGHT_COST = 10
    private const val DIAGONAL_COST = 14

    private var layer: TiledLayer? = null
    private var tileSize = 0f
    private var start: Point? = null
    private var end: Point? = null
    private var walkableCount = 0
    private val tileRect = RectF()
    private var nodes: Array<Node?> = emptyArray()
    private val openNodes = ArrayList<Node>()
    private var currentNode: Node? = null
    private var elapsedTime = 0f
    private var searchFinished = false
    private var searchFailed = false

    fun setTiledLayer(layer: TiledLayer, tileSize: Float) {
        this.layer = layer
        this.tileSize = tileSize
        scanMarkerLayer(layer)
        resetSearch(layer)
        Log.d(TAG, "scan: start=$start, end=$end, walkable=$walkableCount, tileSize=$tileSize")
    }

    fun update(frameTime: Float) {
        if (searchFinished || searchFailed) return

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
        drawStartEndTiles(canvas)
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
            Log.d(TAG, "A* finished: g=${current.g}")
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
                }
            }
            i++
        }
    }

    private fun drawStartEndTiles(canvas: Canvas) {
        start?.let { drawTile(canvas, it.x, it.y, startPaint) }
        end?.let { drawTile(canvas, it.x, it.y, endPaint) }
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
}
