package kr.ac.tukorea.ge.spgp2026.tudefence.game.map

import android.graphics.Point
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
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
    private val tileRect = RectF()

    fun setTiledLayer(layer: TiledLayer, tileSize: Float) {
        this.layer = layer
        this.tileSize = tileSize
        scanMarkerLayer(layer)
        Log.d(TAG, "scan: start=$start, end=$end, walkable=$walkableCount, tileSize=$tileSize")
    }

    fun draw(canvas: Canvas) {
        val layer = layer ?: return
        drawMarkerTiles(canvas, layer)
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

    private fun isWalkable(gid: Int): Boolean {
        return gid == PATH_TILE || gid == START_TILE || gid == END_TILE
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

    private val walkablePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x407F7FFF // semi-transparent violet
    }
    private val startPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x8000FF00.toInt() // semi-transparent green
    }
    private val endPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x80FF0000.toInt() // semi-transparent red
    }
}
