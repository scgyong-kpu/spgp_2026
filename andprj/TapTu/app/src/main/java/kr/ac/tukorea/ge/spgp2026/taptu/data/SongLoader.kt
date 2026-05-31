package kr.ac.tukorea.ge.spgp2026.taptu.data

import android.content.res.AssetManager
import kotlinx.serialization.json.Json

object SongLoader {
    private const val SONGS_JSON = "songs.json"

    private val json = Json {
        // chart_grab.js 가 만든 songs.json 에 필드가 더 늘어나도,
        // 현재 Song data class 에 선언한 값만 먼저 읽을 수 있게 한다.
        ignoreUnknownKeys = true
    }

    fun load(assets: AssetManager): List<Song> {
        val text = assets.open(SONGS_JSON).bufferedReader().use { it.readText() }
        return json.decodeFromString<List<Song>>(text)
    }
}
