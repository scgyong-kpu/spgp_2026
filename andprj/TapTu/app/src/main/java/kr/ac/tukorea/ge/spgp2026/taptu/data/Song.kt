package kr.ac.tukorea.ge.spgp2026.taptu.data

import kotlinx.serialization.Serializable

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
) {
    override fun toString(): String {
        return "[$rank] $title / $artist <$album>"
    }
}
