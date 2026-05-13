package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.enemy

import android.graphics.Rect
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.SheetSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R
import kr.ac.tukorea.ge.spgp2026.tudefence.game.scene.main.MainScene

class Fly private constructor(gctx: GameContext):
    SheetSprite(gctx, R.mipmap.galaga_flies, 2f), IRecyclable
{
    enum class Type(
        val health: Float,
        val spawnRate: Int,
    ) {
        BOSS(150f, 0),
        RED(50f, 10),
        BLUE(30f, 20),
        CYAN(20f, 30),
        DRAGON(10f, 40),
    }

    init {
        prepareFrameRects()
        setSize(SIZE, SIZE)
    }

    var life = 0f
        private set
    var maxLife = 0f
        private set
    private fun init(type: Type): Fly {
        frameRects = rectsArray[type.ordinal]
        life = type.health
        maxLife = life
        return this
    }

    override fun onRecycle() {}

    private fun prepareFrameRects() {
        // galaga_flies 이미지는 type 별 frame 들이 한 줄로 이어진 sprite sheet 이다.
        // Rect 목록은 모든 Fly 인스턴스가 공유해도 되는 읽기 전용 정보이므로 companion cache 에 한 번만 만든다.
        if (rectsArray.isNotEmpty()) return

        // frame 크기는 bitmap.height 를 기준으로 계산한다.
        // bitmap 은 SheetSprite/Sprite 생성이 끝난 뒤에 준비되므로,
        // companion object 초기화 시점이 아니라 첫 Fly 인스턴스 init 시점에 cache 를 채운다.
        val frameSize = bitmap.height
        var x = 0
        for (i in 0 until Type.entries.size) {
            val rects = ArrayList<Rect>()
            for (j in 0 until FRAME_COUNT) {
                rects.add(Rect(x, 0, x + frameSize, frameSize))
                x += frameSize
            }
            rectsArray.add(rects)
        }
    }

    companion object {
        fun get(gctx: GameContext, type: Type): Fly {
            val world = (gctx.scene as MainScene).world
            val fly = world.obtain(Fly::class.java) ?: Fly(gctx)
            return fly.init(type)
        }

        // type 별 animation frame rect 목록이다.
        // 생성 후에는 수정하지 않는 cache 이지만, bitmap.height 를 알아야 만들 수 있어
        // 첫 Fly 인스턴스 생성 시 prepareFrameRects() 에서 채운다.
        private val rectsArray = mutableListOf<ArrayList<Rect>>()

        const val SIZE = 200f
        private const val FRAME_COUNT = 2
    }
}
