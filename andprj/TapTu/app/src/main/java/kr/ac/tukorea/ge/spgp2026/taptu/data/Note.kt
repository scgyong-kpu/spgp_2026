package kr.ac.tukorea.ge.spgp2026.taptu.data

// note file 의 한 줄에서 읽어 낸 note 하나를 나타낸다.
// pret 는 어느 입력 위치/라인에 해당하는지를 뜻하고,
// time 은 음악 시작 후 몇 초에 이 note 가 처리되어야 하는지를 뜻한다.
data class Note(
    val pret: Int,
    val time: Float,
) {
    companion object {
        // note data line 은 "N 2 9441" 처럼 적는다.
        // 첫 번째 숫자는 pret, 두 번째 숫자는 millisecond 단위 시간이다.
        private val noteLineRegex = Regex("""^N\s+(\d+)\s+(\d+)\s*$""")

        fun parse(line: String): Note? {
            val matchResult = noteLineRegex.matchEntire(line) ?: return null
            val pret = matchResult.groupValues[1].toInt()
            val millis = matchResult.groupValues[2].toInt()
            return Note(
                pret = pret,
                time = millis / 1000.0f,
            )
        }
    }
}
