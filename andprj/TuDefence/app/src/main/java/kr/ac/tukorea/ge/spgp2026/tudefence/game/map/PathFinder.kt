package kr.ac.tukorea.ge.spgp2026.tudefence.game.map

import android.graphics.Point
import android.util.Log

object PathFinder {
    private const val TAG = "PathFinder"
    private const val PATH_TILE = 30
    private const val START_TILE = 31
    private const val END_TILE = 46

    private var layer: TiledLayer? = null
    private var tileSize = 0f
    private var start: Point? = null
    private var end: Point? = null
    private var walkableCount = 0

    fun setTiledLayer(layer: TiledLayer, tileSize: Float) {
        this.layer = layer
        this.tileSize = tileSize
        scanMarkerLayer(layer)
        Log.d(TAG, "scan: start=$start, end=$end, walkable=$walkableCount, tileSize=$tileSize")
    }

    private fun scanMarkerLayer(layer: TiledLayer) {
        start = null
        end = null
        walkableCount = 0

        var y = 0
        while (y < layer.height) {
            var x = 0
            while (x < layer.width) {
                val gid = layer.tileAt(x, y)
                if (isWalkable(gid)) {
                    walkableCount++
                }
                when (gid) {
                    START_TILE -> start = Point(x, y)
                    END_TILE -> end = Point(x, y)
                }
                x++
            }
            y++
        }

        checkNotNull(start) { "Marker layer must contain start tile gid=$START_TILE" }
        checkNotNull(end) { "Marker layer must contain end tile gid=$END_TILE" }
    }

    private fun isWalkable(gid: Int): Boolean {
        return gid == PATH_TILE || gid == START_TILE || gid == END_TILE
    }
}
