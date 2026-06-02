package kr.ac.tukorea.ge.spgp2026.taptu.data

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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

    @Transient
    private var notes: List<Note>? = null

    @Transient
    var noteLength: Float = 0f
        private set

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

    fun loadNotes(assets: AssetManager): List<Note> {
        notes?.let { return it }

        // note 파일도 mp3/thumbnail 처럼 rank 에 맞춘 asset 파일명 규칙을 사용한다.
        // 예를 들어 rank 가 8 이면 assets/notes/n_008.txt 를 읽는다.
        val filename = "notes/n_%03d.txt".format(rank)
        val loadedNotes = mutableListOf<Note>()
        var length = 0f

        runCatching {
            assets.open(filename).bufferedReader().useLines { lines ->
                for (line in lines) {
                    // T Drowning 같은 제목 줄이나 아직 지원하지 않는 줄은 parse 결과가 null 이므로 무시한다.
                    val note = Note.parse(line) ?: continue
                    loadedNotes.add(note)
                    if (length < note.time) {
                        length = note.time
                    }
                }
            }
        }.onFailure {
            Log.w(javaClass.simpleName, "Cannot load note file: $filename", it)
        }

        noteLength = length
        notes = loadedNotes
        Log.d(javaClass.simpleName, "loaded ${loadedNotes.size} notes from $filename, length=$noteLength")
        return loadedNotes
    }

    override fun toString(): String {
        return "[$rank] $title / $artist <$album>"
    }
}
