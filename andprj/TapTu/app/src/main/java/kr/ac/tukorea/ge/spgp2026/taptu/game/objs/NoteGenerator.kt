package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import android.graphics.Canvas
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.data.Song
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.MainLayer

// NoteGenerator 는 "언제 NoteSprite 를 만들 것인가"만 담당한다.
// MainScene 이 직접 note 를 꺼내고 Sprite 를 만드는 방식도 가능하지만,
// 그렇게 하면 Scene 이 음악 재생, 배경, note 생성 책임을 모두 갖게 된다.
// 여기서는 현재 음악 시간을 읽는 함수만 받아, 생성 규칙을 별도 GameObject 로 분리한다.
class NoteGenerator(
    private val song: Song,
    private val world: World<MainLayer>,
    private val musicTimeProvider: () -> Float,
) : IGameObject {
    override fun update(gctx: GameContext) {
        val visibleUntil = musicTimeProvider() + NoteSprite.screenfulTime()
        while (true) {
            val note = song.popNoteBefore(visibleUntil) ?: break
            Log.d(javaClass.simpleName, "Note: $note")
            world.add(NoteSprite.get(gctx, note, musicTimeProvider), MainLayer.NOTE)
        }
    }

    override fun draw(canvas: Canvas) {
        // NoteGenerator 는 화면에 보이는 객체가 아니라 note 생성 규칙을 실행하는 controller 이다.
    }
}
