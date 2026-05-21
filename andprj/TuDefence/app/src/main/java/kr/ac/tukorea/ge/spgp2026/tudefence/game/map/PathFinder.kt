package kr.ac.tukorea.ge.spgp2026.tudefence.game.map

import android.graphics.Path
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

object PathFinder {
    private const val PATH_TILE = 30
    private const val START_TILE = 31
    private const val END_TILE = 46
    private const val STRAIGHT_COST = 10
    private const val DIAGONAL_COST = 14

    private var layer: TiledLayer? = null
    private var tileSize = 0f
    private var startX = INVALID_TILE
    private var startY = INVALID_TILE
    private var endX = INVALID_TILE
    private var endY = INVALID_TILE
    private var nodes: Array<Node?> = emptyArray()
    private val openNodes = ArrayList<Node>()

    private var simplifiedCount = 0
    private var simplifiedXs = IntArray(0)
    private var simplifiedYs = IntArray(0)
    private var waypointCount = 0
    private var waypointXs = FloatArray(0)
    private var waypointYs = FloatArray(0)
    private val fromTangent = FloatArray(2)
    private val toTangent = FloatArray(2)

    fun setTiledLayer(layer: TiledLayer, tileSize: Float) {
        this.layer = layer
        this.tileSize = tileSize
        scanMarkerLayer(layer)
        buildNodes(layer)
        findPath()
    }

    fun createRandomizedPath(toPath: Path): Boolean {
        val layer = layer ?: return false
        if (simplifiedCount == 0) return false

        // simplified path 는 stage 가 바뀌지 않는 한 동일하다.
        // Fly 마다 달라지는 waypoint offset 만 reusable array 에 다시 채워,
        // Fly 생성/재활용 시점의 작은 객체 생성을 피한다.
        buildRandomizedWaypoints(layer)
        buildPathFromWaypoints(toPath)
        return true
    }

    private fun scanMarkerLayer(layer: TiledLayer) {
        val startIndex = layer.data.indexOf(START_TILE)
        val endIndex = layer.data.indexOf(END_TILE)
        check(startIndex >= 0) { "Marker layer must contain start tile gid=$START_TILE" }
        check(endIndex >= 0) { "Marker layer must contain end tile gid=$END_TILE" }

        startX = startIndex % layer.width
        startY = startIndex / layer.width
        endX = endIndex % layer.width
        endY = endIndex / layer.width
    }

    private fun buildNodes(layer: TiledLayer) {
        nodes = arrayOfNulls(layer.width * layer.height)
        openNodes.clear()
        simplifiedCount = 0
        waypointCount = 0

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
    }

    private fun findPath() {
        val layer = layer ?: return
        val startNode = nodeAt(layer, startX, startY) ?: return
        startNode.g = 0
        startNode.h = heuristic(startX, startY, endX, endY)
        startNode.opened = true
        openNodes.add(startNode)

        while (openNodes.isNotEmpty()) {
            val current = popBestOpenNode()
            current.closed = true

            if (current.x == endX && current.y == endY) {
                buildSimplifiedPath(current)
                releaseSearchMemory()
                return
            }

            visitNeighbors(layer, current)
        }

        releaseSearchMemory()
    }

    private fun releaseSearchMemory() {
        nodes = emptyArray()
        openNodes.clear()
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

    private fun visitNeighbors(layer: TiledLayer, current: Node) {
        var dir = 0
        while (dir < DX.size) {
            val nx = current.x + DX[dir]
            val ny = current.y + DY[dir]
            val neighbor = nodeAt(layer, nx, ny)
            if (neighbor != null && !neighbor.closed) {
                visitNeighbor(current, neighbor, MOVE_COST[dir])
            }
            dir++
        }
    }

    private fun visitNeighbor(current: Node, neighbor: Node, moveCost: Int) {
        val tentativeG = current.g + moveCost
        if (neighbor.opened && tentativeG >= neighbor.g) return

        neighbor.parent = current
        neighbor.g = tentativeG
        neighbor.h = heuristic(neighbor.x, neighbor.y, endX, endY)

        if (!neighbor.opened) {
            neighbor.opened = true
            openNodes.add(neighbor)
        }
    }

    private fun buildSimplifiedPath(endNode: Node) {
        simplifiedCount = countSimplifiedPath(endNode)
        ensureSimplifiedCapacity(simplifiedCount)

        var writeIndex = simplifiedCount - 1
        var node: Node? = endNode
        var previous: Node? = null
        while (node != null) {
            val parent = node.parent
            if (keepsSimplifiedNode(node, previous, parent)) {
                simplifiedXs[writeIndex] = node.x
                simplifiedYs[writeIndex] = node.y
                writeIndex--
            }
            previous = node
            node = parent
        }
    }

    private fun countSimplifiedPath(endNode: Node): Int {
        var count = 0
        var node: Node? = endNode
        var previous: Node? = null
        while (node != null) {
            val parent = node.parent
            if (keepsSimplifiedNode(node, previous, parent)) {
                count++
            }
            previous = node
            node = parent
        }
        return count
    }

    private fun keepsSimplifiedNode(node: Node, previous: Node?, parent: Node?): Boolean {
        if (previous == null || parent == null) return true

        val dxToPrevious = previous.x - node.x
        val dyToPrevious = previous.y - node.y
        val dxFromParent = node.x - parent.x
        val dyFromParent = node.y - parent.y
        return dxToPrevious != dxFromParent || dyToPrevious != dyFromParent
    }

    private fun ensureSimplifiedCapacity(size: Int) {
        if (simplifiedXs.size >= size) return
        simplifiedXs = IntArray(size)
        simplifiedYs = IntArray(size)
    }

    private fun buildRandomizedWaypoints(layer: TiledLayer) {
        waypointCount = simplifiedCount + 2
        ensureWaypointCapacity(waypointCount)
        if (simplifiedCount == 0) return

        var i = 0
        while (i < simplifiedCount) {
            val writeIndex = i + 1
            waypointXs[writeIndex] = simplifiedXs[i] + randomOffset()
            waypointYs[writeIndex] = simplifiedYs[i] + randomOffset()
            i++
        }

        val firstY = waypointYs[1]
        val lastY = waypointYs[simplifiedCount]
        waypointXs[0] = -0.5f
        waypointYs[0] = firstY
        waypointXs[waypointCount - 1] = layer.width + 0.5f
        waypointYs[waypointCount - 1] = lastY
    }

    private fun ensureWaypointCapacity(size: Int) {
        if (waypointXs.size >= size) return
        waypointXs = FloatArray(size)
        waypointYs = FloatArray(size)
    }

    private fun randomOffset(): Float {
        return MIN_WAYPOINT_OFFSET + Random.nextFloat() * (MAX_WAYPOINT_OFFSET - MIN_WAYPOINT_OFFSET)
    }

    private fun buildPathFromWaypoints(path: Path) {
        if (waypointCount == 0) return

        path.reset()
        path.moveTo(waypointXs[0] * tileSize, waypointYs[0] * tileSize)
        if (waypointCount == 1) return

        var i = 1
        while (i < waypointCount) {
            tangentAt(i - 1, fromTangent)
            tangentAt(i, toTangent)
            val controlDistance = controlDistanceBetween(i - 1, i)
            val fromX = waypointXs[i - 1]
            val fromY = waypointYs[i - 1]
            val toX = waypointXs[i]
            val toY = waypointYs[i]

            path.cubicTo(
                fromX * tileSize + fromTangent[0] * controlDistance,
                fromY * tileSize + fromTangent[1] * controlDistance,
                toX * tileSize - toTangent[0] * controlDistance,
                toY * tileSize - toTangent[1] * controlDistance,
                toX * tileSize,
                toY * tileSize,
            )
            i++
        }
    }

    private fun controlDistanceBetween(fromIndex: Int, toIndex: Int): Float {
        val dx = waypointXs[toIndex] - waypointXs[fromIndex]
        val dy = waypointYs[toIndex] - waypointYs[fromIndex]
        val segmentLengthInTiles = sqrt(dx * dx + dy * dy)
        val controlDistanceInTiles = min(
            segmentLengthInTiles / CUBIC_CONTROL_DISTANCE_DIVISOR,
            CUBIC_CONTROL_DISTANCE_MAX_IN_TILES,
        )
        return controlDistanceInTiles * tileSize
    }

    private fun tangentAt(index: Int, out: FloatArray) {
        val previousIndex = if (index == 0) index else index - 1
        val nextIndex = if (index == waypointCount - 1) index else index + 1
        val dx = waypointXs[nextIndex] - waypointXs[previousIndex]
        val dy = waypointYs[nextIndex] - waypointYs[previousIndex]
        val length = sqrt(dx * dx + dy * dy)
        if (length == 0f) {
            out[0] = 0f
            out[1] = 0f
            return
        }
        out[0] = dx / length
        out[1] = dy / length
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

    private class Node(val x: Int, val y: Int) {
        var g = Int.MAX_VALUE
        var h = 0
        val f: Int
            get() = g + h
        var parent: Node? = null
        var opened = false
        var closed = false
    }

    private val DX = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
    private val DY = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    private val MOVE_COST = intArrayOf(
        DIAGONAL_COST, STRAIGHT_COST, DIAGONAL_COST,
        STRAIGHT_COST, STRAIGHT_COST,
        DIAGONAL_COST, STRAIGHT_COST, DIAGONAL_COST,
    )
    private const val INVALID_TILE = -1
    private const val CUBIC_CONTROL_DISTANCE_DIVISOR = 4f
    private const val CUBIC_CONTROL_DISTANCE_MAX_IN_TILES = 1.0f
    private const val MIN_WAYPOINT_OFFSET = 0.2f
    private const val MAX_WAYPOINT_OFFSET = 0.8f
}
