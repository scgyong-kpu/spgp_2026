package kr.ac.tukorea.ge.spgp2026.taptu.data

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// songs.json 의 한 원소에 대응하는 data class 이다.
// @Serializable 을 붙이면 kotlinx.serialization compiler plugin 이
// JSON 에서 Song 객체를 만들기 위한 코드를 build time 에 생성한다.
@Serializable
data class Song(
    val rank: Int,
    val title: String,
    val artist: String,
    val album: String,
    val thumbnail: String,
    val demoStart: Int = 0,
    val demoEnd: Int = 0,
) {
    val mp3AssetPath: String
        get() = "mp3/r_%03d.mp3".format(rank)

    @Transient
    private var thumbnailBitmap: Bitmap? = null

    fun loadThumbnail(assets: AssetManager): Bitmap? {
        thumbnailBitmap?.let { return it }

        // songs.json 에는 원본 thumbnail URL 이 남아 있지만,
        // 앱에서는 미리 받아 둔 assets/thumbnails/cover_001.jpg 형식의 파일을 사용한다.
        // rank 는 1부터 시작하므로 파일명도 cover_001, cover_002 처럼 맞춘다.
        val filename = "thumbnails/cover_%03d.jpg".format(rank)
        thumbnailBitmap = runCatching {
            assets.open(filename).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }.getOrNull()
        return thumbnailBitmap
    }

    override fun toString(): String {
        return "[$rank] $title / $artist <$album>"
    }
}
