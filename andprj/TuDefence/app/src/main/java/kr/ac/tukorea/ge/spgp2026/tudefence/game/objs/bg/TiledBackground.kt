package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.bg

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.MapCamera
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledLayer
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledMap
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledMapLoader
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.TiledTileset
import kotlin.math.ceil
import kotlin.math.floor

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

    // 확대/이동은 TiledBackground 가 직접 처리하지 않고 MapCamera 가 담당한다.
    // 이 객체는 camera 가 알려주는 visibleMapRect 를 보고 필요한 tile 만 고른 뒤,
    // destination rect 는 map 좌표로 설정한다. 실제 화면 변환은 CameraBegin 의 canvas matrix 가 처리한다.
    private val mapCamera: MapCamera,

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
    // TuDefence 는 32x18 map 을 1600x900 좌표계에 맞추기 위해 50x50 으로 그린다.
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
        val visibleRect = mapCamera.visibleMapRect
        val startTileX = floor(visibleRect.left / tileWidth).toInt().coerceAtLeast(0)
        val startTileY = floor(visibleRect.top / tileHeight).toInt().coerceAtLeast(0)
        val endTileX = ceil(visibleRect.right / tileWidth).toInt().coerceAtMost(layer.width)
        val endTileY = ceil(visibleRect.bottom / tileHeight).toInt().coerceAtMost(layer.height)

        // visibleMapRect 는 "현재 화면에 보이는 map 좌표 범위"이다.
        // 따라서 이 범위와 겹치는 tile index 만 순회하면 화면 밖 tile 은 drawBitmap() 호출 자체를 하지 않는다.
        // destination rect 는 map 좌표로 설정하고, 화면으로의 확대/이동 변환은 CameraBegin 의 matrix 에 맡긴다.
        //
        // for-each 대신 while 을 쓰는 이유는 draw() 가 매 프레임 호출되는 hot path 이기 때문이다.
        // iterator 객체 생성을 피하고, tileX/tileY 를 직접 증가시키는 편이 의도가 분명하다.
        var tileY = startTileY
        while (tileY < endTileY) {
            drawRow(canvas, startTileX, endTileX, tileY)
            tileY++
        }
    }

    private fun drawRow(canvas: Canvas, startTileX: Int, endTileX: Int, tileY: Int) {
        // 한 줄 안에서는 visibleMapRect 와 겹치는 tile 만 왼쪽에서 오른쪽으로 그린다.
        var tileX = startTileX
        while (tileX < endTileX) {
            drawTile(canvas, tileX, tileY)
            tileX++
        }
    }

    private fun drawTile(canvas: Canvas, tileX: Int, tileY: Int) {
        // gid 는 Tiled layer.data 에 저장된 tile 번호이다.
        // Tiled 에서 gid == 0 은 "빈 칸"을 의미하므로 drawBitmap() 을 호출하지 않는다.
        val gid = layer.tileAt(tileX, tileY)
        if (gid == 0) return

        // source rect 는 tileset bitmap 안에서 잘라낼 위치이고,
        // destination rect 는 map 좌표계에서 그려질 위치이다.
        // 이미 canvas 에 mapCamera.matrix 가 concat 되어 있으므로 drawBitmap() 결과는 화면 좌표로 변환된다.
        setSourceRect(gid)
        val left = tileX * tileWidth
        val top = tileY * tileHeight
        dstRect.set(left, top, left + tileWidth, top + tileHeight)
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
    }
}
