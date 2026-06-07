package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.data.Song
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.mainWorld

class PretBg(gctx: GameContext,
             private val song: Song,
             private val musicTimeProvider: () -> Float,
): Sprite(gctx, kr.ac.tukorea.ge.spgp2026.taptu.R.mipmap.bg) {
    private var nextNoteIndex = 0

    init {
        val screenWidth = gctx.metrics.width
        val screenHeight = gctx.metrics.height
        val centerX = screenWidth / 2
        val centerY = screenHeight / 2
        setCenterProportionalWidth(centerX, centerY, screenWidth)
    }

    override fun update(gctx: GameContext) {
        val musicTime = musicTimeProvider()

        // PretBg 는 note 생성자가 아니라 "현재 음악 시간이 note timing 을 지나갔는지"를 관찰한다.
        // Song.popNoteBefore() 를 쓰면 NoteGenerator 가 쓰는 생성용 index 까지 같이 움직이므로,
        // 여기서는 Song.noteAt(index) 로 들여다보고 PretBg 만의 nextNoteIndex 를 따로 전진시킨다.
        while (true) {
            val note = song.noteAt(nextNoteIndex) ?: break
            if (note.time > musicTime) break

            val x = NoteSprite.xFromPret(note.pret)
            gctx.mainWorld().add(Explosion.get(gctx, x, NoteSprite.GOAL_Y), MainLayer.EXPLOSION)
            Log.d(
                javaClass.simpleName,
                "Passed note #$nextNoteIndex: pret=${note.pret}, time=${"%.3f".format(note.time)}, musicTime=${"%.3f".format(musicTime)}"
            )
            nextNoteIndex++
        }
    }
}
