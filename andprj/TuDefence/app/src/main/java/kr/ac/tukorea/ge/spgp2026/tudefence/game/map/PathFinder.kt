package kr.ac.tukorea.ge.spgp2026.tudefence.game.map

import android.graphics.Path
import android.graphics.Point
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
    private var start: Point? = null
    private var end: Point? = null
    private var nodes: Array<Node?> = emptyArray()
    private val openNodes = ArrayList<Node>()
    private val simplifiedPath = ArrayList<Node>()
    private val waypoints = ArrayList<Waypoint>()

    fun setTiledLayer(layer: TiledLayer, tileSize: Float) {
        this.layer = layer
        this.tileSize = tileSize
        scanMarkerLayer(layer)
        buildNodes(layer)
        findPath()
    }

    fun createRandomizedPath(toPath: Path): Boolean {
        val layer = layer ?: return false
        if (simplifiedPath.isEmpty()) return false

        // simplifiedPath 는 marker layer 가 바뀌지 않는 한 모든 Fly 가 공유할 수 있는 결과이다.
        // waypoint 의 tile 내부 offset 만 init() 때마다 새로 뽑아 Fly 별 path 차이를 만든다.
        buildRandomizedWaypoints(layer)
        buildPathFromWaypoints(toPath)
        return true
    }

    private fun scanMarkerLayer(layer: TiledLayer) {
        val startIndex = layer.data.indexOf(START_TILE)
        val endIndex = layer.data.indexOf(END_TILE)
        check(startIndex >= 0) { "Marker layer must contain start tile gid=$START_TILE" }
        check(endIndex >= 0) { "Marker layer must contain end tile gid=$END_TILE" }

        start = Point(startIndex % layer.width, startIndex / layer.width)
        end = Point(endIndex % layer.width, endIndex / layer.width)
    }

    private fun buildNodes(layer: TiledLayer) {
        nodes = arrayOfNulls(layer.width * layer.height)
        openNodes.clear()
        simplifiedPath.clear()
        waypoints.clear()

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
        val start = start ?: return
        val end = end ?: return
        val startNode = nodeAt(layer, start.x, start.y) ?: return
        startNode.g = 0
        startNode.h = heuristic(start.x, start.y, end.x, end.y)
        startNode.opened = true
        openNodes.add(startNode)

        while (openNodes.isNotEmpty()) {
            val current = popBestOpenNode()
            current.closed = true

            if (current.x == end.x && current.y == end.y) {
                buildSimplifiedPath(current)
                return
            }

            visitNeighbors(layer, current, end)
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

    private fun visitNeighbors(layer: TiledLayer, current: Node, end: Point) {
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

    private fun buildSimplifiedPath(endNode: Node) {
        simplifiedPath.clear()

        // parent chain 은 end -> start 순서이다.
        // raw path 전체를 따로 보관하지 않고, 방향이 바뀌는 지점만 모았다가 마지막에 뒤집는다.
        var node: Node? = endNode
        var previous: Node? = null
        while (node != null) {
            val parent = node.parent
            if (previous == null) {
                simplifiedPath.add(node)
            } else if (parent != null) {
                val dxToPrevious = previous.x - node.x
                val dyToPrevious = previous.y - node.y
                val dxFromParent = node.x - parent.x
                val dyFromParent = node.y - parent.y
                if (dxToPrevious != dxFromParent || dyToPrevious != dyFromParent) {
                    simplifiedPath.add(node)
                }
            } else {
                simplifiedPath.add(node)
            }

            previous = node
            node = parent
        }
        simplifiedPath.reverse()
    }

    private fun buildRandomizedWaypoints(layer: TiledLayer) {
        waypoints.clear()
        if (simplifiedPath.isEmpty()) return

        var i = 0
        while (i < simplifiedPath.size) {
            val node = simplifiedPath[i]
            waypoints.add(Waypoint(node.x + randomOffset(), node.y + randomOffset()))
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
        if (waypoints.size == 1) return

        var i = 1
        while (i < waypoints.size) {
            val from = waypoints[i - 1]
            val to = waypoints[i]
            val fromTangent = tangentAt(i - 1)
            val toTangent = tangentAt(i)
            val controlDistance = controlDistanceBetween(from, to)

            path.cubicTo(
                from.x * tileSize + fromTangent.x * controlDistance,
                from.y * tileSize + fromTangent.y * controlDistance,
                to.x * tileSize - toTangent.x * controlDistance,
                to.y * tileSize - toTangent.y * controlDistance,
                to.x * tileSize,
                to.y * tileSize,
            )
            i++
        }
    }

    private fun controlDistanceBetween(from: Waypoint, to: Waypoint): Float {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val segmentLengthInTiles = sqrt(dx * dx + dy * dy)
        val controlDistanceInTiles = min(
            segmentLengthInTiles / CUBIC_CONTROL_DISTANCE_DIVISOR,
            CUBIC_CONTROL_DISTANCE_MAX_IN_TILES,
        )
        return controlDistanceInTiles * tileSize
    }

    private fun tangentAt(index: Int): UnitVector {
        val previous = if (index == 0) waypoints[index] else waypoints[index - 1]
        val next = if (index == waypoints.lastIndex) waypoints[index] else waypoints[index + 1]
        val dx = next.x - previous.x
        val dy = next.y - previous.y
        val length = sqrt(dx * dx + dy * dy)
        if (length == 0f) return UnitVector(0f, 0f)
        return UnitVector(dx / length, dy / length)
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

    private class Waypoint(val x: Float, val y: Float)

    private class UnitVector(val x: Float, val y: Float)

    private val DX = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
    private val DY = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    private val MOVE_COST = intArrayOf(
        DIAGONAL_COST, STRAIGHT_COST, DIAGONAL_COST,
        STRAIGHT_COST, STRAIGHT_COST,
        DIAGONAL_COST, STRAIGHT_COST, DIAGONAL_COST,
    )
    private const val CUBIC_CONTROL_DISTANCE_DIVISOR = 4f
    private const val CUBIC_CONTROL_DISTANCE_MAX_IN_TILES = 1.0f
    private const val MIN_WAYPOINT_OFFSET = 0.2f
    private const val MAX_WAYPOINT_OFFSET = 0.8f
}
