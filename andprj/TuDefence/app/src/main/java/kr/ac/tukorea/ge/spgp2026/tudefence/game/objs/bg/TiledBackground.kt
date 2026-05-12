package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.bg

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledLayer
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledMap
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledMapLoader
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledTileset

// TiledBackground 는 Tiled 가 만든 .tmj map 과 tileset image 를 읽어 배경 tile 을 그린다.
// 아직 a2dg 공통 class 로 올리지 않고 app/game.objs.bg 에 두는 이유는,
// TuDefence 수업 진행 중 필요한 기능과 API 모양을 먼저 확인하기 위해서이다.
//
// Tiled map 을 그리는 핵심은 두 단계이다.
// 1. layer.data 에 저장된 gid 를 읽어 "몇 번 tile 을 그릴지" 결정한다.
// 2. 그 gid 를 tileset image 안의 source rect 로 바꾸어 Canvas 에 그린다.
class TiledBackground(
    // gctx 는 asset 접근, 화면 크기(metrics), 나중의 resource 접근을 위해 보관한다.
    private val gctx: GameContext,

    // assets/ 아래의 TMJ 파일 경로이다. 예: "map/desert.tmj"
    mapAssetPath: String,

    // 게임 좌표계에서 tile 하나를 몇 x 몇 크기로 그릴지 나타낸다.
    // TMJ 원본 tile 크기(tilewidth/tileheight)와 화면에 그릴 크기는 다를 수 있다.
    private var tileWidth: Float,
    private var tileHeight: Float,
) : IGameObject {
    // TMJ 파일을 읽어 Kotlin data class 로 변환한다.
    // 이 객체에는 map 크기, layer data, tileset image 이름 같은 정보가 들어 있다.
    private val map: TiledMap = TiledMapLoader.load(gctx.view.context.assets, mapAssetPath)

    // TMJ 안의 image 경로는 보통 TMJ 파일 위치를 기준으로 상대 경로로 저장된다.
    // "map/desert.tmj" 에서 directory 를 구해 두면 "map/" + "tmw_desert_spacing.png" 로 image 를 찾을 수 있다.
    private val assetDirectory = directoryOf(mapAssetPath)

    // 일단 첫 tile layer 와 첫 tileset 만 사용한다.
    // 이후 여러 layer/tileset 이 필요한 단계가 오면 setActiveLayer(), setActiveTileset() 으로 바꿀 수 있다.
    private var layer: TiledLayer = map.firstTileLayer()
    private var tileset: TiledTileset = map.tilesets.first()
    private var bitmap: Bitmap = loadBitmapAsset(assetDirectory + tileset.image)

    // draw() 는 매 프레임 호출되므로 Rect/RectF 를 tile 마다 새로 만들지 않고 재사용한다.
    private val srcRect = Rect()
    private val dstRect = RectF()

    // scrollX/Y 는 "map 의 어느 좌표부터 화면 왼쪽 위에 보이게 할 것인가"를 뜻한다.
    // 예를 들어 scrollX 가 tileWidth 만큼 증가하면 화면은 오른쪽으로 한 tile 이동한 위치를 보게 된다.
    private var scrollX = 0f
    private var scrollY = 0f

    // wraps 가 true 이면 map 끝을 넘어가도 처음으로 이어서 그린다.
    // 무한 반복 배경에는 유용하지만, 타워 디펜스 map 처럼 고정된 판에는 보통 false 로 둔다.
    var wraps = false

    // 외부에서 scroll 위치를 직접 지정할 수 있게 해 둔다.
    // 지금 단계에서는 0,0 이지만, 나중에 카메라 이동이나 스크롤 map 을 실험할 때 사용할 수 있다.
    fun scrollTo(x: Float, y: Float) {
        scrollX = x
        scrollY = y
    }

    // TMJ 에 여러 layer 가 있을 때 그릴 layer 를 고른다.
    // 아직은 첫 layer 만 쓰지만, path/debug/object layer 를 분리할 때 확장 가능하다.
    fun setActiveLayer(index: Int) {
        layer = map.layers[index]
    }

    // TMJ 에 여러 tileset 이 있을 때 사용할 tileset 을 고른다.
    // tileset 이 바뀌면 source image 도 달라지므로 bitmap 을 다시 읽는다.
    fun setActiveTileset(index: Int) {
        tileset = map.tilesets[index]
        bitmap = loadBitmapAsset(assetDirectory + tileset.image)
    }

    // 원본 TMJ tile 크기와 별개로 게임 좌표계에서 보이는 tile 크기를 바꾼다.
    // TuDefence 는 32x18 map 을 3200x1800 좌표계에 맞추기 위해 100x100 으로 그린다.
    fun setTileSize(width: Float, height: Float) {
        tileWidth = width
        tileHeight = height
    }

    fun fullWidth(): Float {
        return map.width * tileWidth
    }

    fun fullHeight(): Float {
        return map.height * tileHeight
    }

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        // wraps 가 켜져 있으면 scroll 값을 map 전체 크기 안으로 접어 넣는다.
        // 예를 들어 fullWidth 가 3200 일 때 scrollX=3300 은 scrollX=100 과 같은 화면을 만든다.
        val fullWidth = fullWidth()
        val fullHeight = fullHeight()
        val effectiveScrollX = if (wraps) wrapped(scrollX, fullWidth) else scrollX
        val effectiveScrollY = if (wraps) wrapped(scrollY, fullHeight) else scrollY

        // 화면 왼쪽 위가 map tile 의 중간부터 시작할 수도 있다.
        // 그래서 첫 tile 은 음수 위치에서 일부만 보일 수 있고,
        // startDx/startDy 는 그 "잘려서 보이는 첫 tile 의 화면 위치"가 된다.
        val startDx = -(effectiveScrollX % tileWidth)
        val startDy = -(effectiveScrollY % tileHeight)

        // scroll 위치가 map 의 몇 번째 tile 에 해당하는지 계산한다.
        // 이후 오른쪽/아래로 진행하며 tile index 를 하나씩 증가시킨다.
        val startTileX = (effectiveScrollX / tileWidth).toInt()
        val startTileY = (effectiveScrollY / tileHeight).toInt()

        // 화면 높이를 채울 때까지 한 줄씩 그린다.
        // for-each 대신 while 을 쓰는 이유는 draw() 가 매 프레임 호출되는 hot path 이기 때문이다.
        // iterator 객체 생성을 피하고, dx/dy/tileX/tileY 를 직접 증가시키는 편이 의도가 분명하다.
        var dy = startDy
        var tileY = startTileY
        while (dy < gctx.metrics.height) {
            drawRow(canvas, startDx, dy, startTileX, tileY)
            dy += tileHeight
            tileY++
        }
    }

    private fun drawRow(canvas: Canvas, startDx: Float, dy: Float, startTileX: Int, tileY: Int) {
        // 한 줄 안에서는 화면 너비를 채울 때까지 왼쪽에서 오른쪽으로 tile 을 그린다.
        // dx 는 화면에 그릴 위치이고, tileX 는 layer.data 에서 읽을 map tile 좌표이다.
        var dx = startDx
        var tileX = startTileX
        while (dx < gctx.metrics.width) {
            drawTile(canvas, tileX, tileY, dx, dy)
            dx += tileWidth
            tileX++
        }
    }

    private fun drawTile(canvas: Canvas, tileX: Int, tileY: Int, dx: Float, dy: Float) {
        // wraps 가 true 이면 tile 좌표도 layer 크기 안으로 접어 넣는다.
        // wraps 가 false 이면 map 바깥 좌표는 그리지 않는다.
        val wrappedTileX = if (wraps) wrapped(tileX, layer.width) else tileX
        val wrappedTileY = if (wraps) wrapped(tileY, layer.height) else tileY
        if (wrappedTileX !in 0 until layer.width || wrappedTileY !in 0 until layer.height) return

        // gid 는 Tiled layer.data 에 저장된 tile 번호이다.
        // Tiled 에서 gid == 0 은 "빈 칸"을 의미하므로 drawBitmap() 을 호출하지 않는다.
        val gid = layer.tileAt(wrappedTileX, wrappedTileY)
        if (gid == 0) return

        // source rect 는 tileset bitmap 안에서 잘라낼 위치이고,
        // destination rect 는 게임 화면 좌표계에서 그려질 위치이다.
        setSourceRect(gid)
        dstRect.set(dx, dy, dx + tileWidth, dy + tileHeight)
        canvas.drawBitmap(bitmap, srcRect, dstRect, null)
    }

    private fun setSourceRect(gid: Int) {
        // TMJ layer data 의 gid 는 tileset.firstgid 부터 시작한다.
        // Bitmap 안에서 몇 번째 tile 인지 계산하려면 firstgid 를 빼서 0-based index 로 바꾼다.
        val tileIndex = gid - tileset.firstgid
        val column = tileIndex % tileset.columns
        val row = tileIndex / tileset.columns
        val left = tileset.margin + column * (tileset.tilewidth + tileset.spacing)
        val top = tileset.margin + row * (tileset.tileheight + tileset.spacing)
        srcRect.set(left, top, left + tileset.tilewidth, top + tileset.tileheight)
    }

    private fun loadBitmapAsset(path: String): Bitmap {
        return gctx.view.context.assets.open(path).use { input ->
            BitmapFactory.decodeStream(input)
        }
    }

    companion object {
        private fun directoryOf(assetPath: String): String {
            val slash = assetPath.lastIndexOf('/')
            return if (slash < 0) "" else assetPath.substring(0, slash + 1)
        }

        // Kotlin 의 % 는 음수 입력에 대해 음수 remainder 를 돌려줄 수 있다.
        // scroll 이 음수가 될 수도 있는 일반 구현에서는 항상 0 <= result < size 가 되도록 보정한다.
        private fun wrapped(value: Float, size: Float): Float {
            val remainder = value % size
            return if (remainder < 0f) remainder + size else remainder
        }

        private fun wrapped(value: Int, size: Int): Int {
            val remainder = value % size
            return if (remainder < 0) remainder + size else remainder
        }
    }
}
