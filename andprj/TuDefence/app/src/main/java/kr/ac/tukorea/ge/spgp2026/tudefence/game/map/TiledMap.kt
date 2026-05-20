package kr.ac.tukorea.ge.spgp2026.tudefence.game.map

import android.content.res.AssetManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Tiled 의 .tmj 파일은 JSON 이므로 Kotlin data class 로 대응시켜 읽을 수 있다.
// @Serializable 은 kotlinx.serialization compiler plugin 에게
// 이 class 의 JSON encoder/decoder 코드를 생성해 달라고 알려 주는 표시이다.
//
// 여기서는 TMJ 전체 필드를 모두 옮기지 않는다.
// 현재 단계에서 배경 tile 을 그리는 데 필요한 필드만 먼저 선언하고,
// object layer, custom property 같은 정보가 필요해지면 그때 property 를 추가한다.
@Serializable
data class TiledMap(
    // map 을 구성하는 tile 의 가로/세로 개수이다.
    val width: Int,
    val height: Int,

    // tile 하나의 원본 pixel 크기이다. 현재 stage map 들은 32x32 tile 을 사용한다.
    val tilewidth: Int,
    val tileheight: Int,

    // Tiled 의 layers 배열이다. 지금은 tilelayer 하나만 사용하지만,
    // 이후 object layer 나 여러 tile layer 를 추가해도 같은 배열에 들어온다.
    val layers: List<TiledLayer>,

    // tile image 정보이다. firstgid 와 columns 를 이용하면 gid 에서 source rect 를 계산할 수 있다.
    val tilesets: List<TiledTileset>,
) {
    fun firstTileLayer(): TiledLayer {
        return layers.first { it.type == "tilelayer" }
    }

    fun tileLayer(name: String): TiledLayer {
        return layers.first { it.type == "tilelayer" && it.name == name }
    }
}

// Tiled 의 layer 중 type == "tilelayer" 인 항목을 위한 class 이다.
// data 에는 tile gid 가 1차원 배열로 저장된다.
// Tiled 는 왼쪽 위부터 오른쪽으로 한 줄씩 저장하므로 index 는 y * width + x 로 계산한다.
@Serializable
data class TiledLayer(
    val name: String,
    val type: String,

    // layer 자체의 tile 개수이다. 보통 map 의 width/height 와 같지만,
    // Tiled format 상 layer 별로 값을 따로 들고 있으므로 그대로 읽어 둔다.
    val width: Int,
    val height: Int,

    // 각 칸에 들어갈 tile gid 목록이다.
    // gid == 0 이면 빈 tile 이고, 1 이상이면 tileset 의 firstgid 를 기준으로 tile 을 찾는다.
    // 범위 밖 좌표는 실제 tile 이 아니므로 -1 을 돌려준다.
    // 이렇게 해 두면 호출부는 contains() 를 따로 부르지 않고 gid 값만 비교할 수 있다.
    val data: List<Int>,
) {
    fun tileAt(x: Int, y: Int): Int {
        if (x !in 0 until width || y !in 0 until height) return -1
        return data[y * width + x]
    }
}

// tilesets 배열의 원소이다.
// 지금은 하나의 tileset 만 쓰지만, TMJ 는 여러 tileset 을 가질 수 있으므로 배열로 읽는다.
@Serializable
data class TiledTileset(
    // TMJ 의 layer data 에 저장된 gid 가 어느 tileset 부터 시작하는지 알려 준다.
    // 예를 들어 firstgid == 1 이면 gid 1 이 이 tileset 의 첫 번째 tile 이다.
    val firstgid: Int,

    // tile image 파일 이름이다. TMJ 파일 위치를 기준으로 저장된다.
    val image: String,

    // tile image 한 줄에 들어 있는 tile 개수이다.
    // gid 에서 row/column 을 계산할 때 필요하다.
    val columns: Int,

    val tilewidth: Int,
    val tileheight: Int,

    // tile image 안에서 tile 사이의 간격과 가장자리 여백이다.
    // desert tileset 은 spacing/margin 을 사용하므로 source rect 계산 때 반드시 반영해야 한다.
    val spacing: Int = 0,
    val margin: Int = 0,
)

object TiledMapLoader {
    private val json = Json {
        // Tiled 가 저장하는 wangsets, tiledversion 같은 부가 정보는 아직 쓰지 않는다.
        // 필요한 필드만 data class 로 선언하고 나머지는 무시하면 단계별 구현을 작게 유지할 수 있다.
        ignoreUnknownKeys = true
    }

    fun load(assets: AssetManager, path: String): TiledMap {
        val text = assets.open(path).bufferedReader().use { it.readText() }
        return json.decodeFromString<TiledMap>(text)
    }
}
