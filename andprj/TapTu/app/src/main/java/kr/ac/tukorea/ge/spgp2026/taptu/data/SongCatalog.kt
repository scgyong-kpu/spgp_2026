package kr.ac.tukorea.ge.spgp2026.taptu.data

import android.content.res.AssetManager

// SongLoader 는 songs.json 을 읽는 역할만 맡고,
// SongCatalog 는 읽어 온 곡 목록을 앱 안에서 보관하는 역할을 맡는다.
// 이렇게 나누면 "파일에서 읽기"와 "현재 앱이 가진 곡 목록"의 책임이 섞이지 않는다.
object SongCatalog {
    var songs: List<Song> = emptyList()
        private set

    fun load(assets: AssetManager) {
        songs = SongLoader.load(assets)
    }
}
