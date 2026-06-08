package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.taptu.data.Note

interface INoteSprite: IGameObject {
    val note: Note
    val x: Float
    val y: Float

    companion object {

    }
}