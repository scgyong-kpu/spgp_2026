package kr.ac.tukorea.ge.spgp2026.taptu.game.objs

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.data.Note
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.mainWorld
import kr.ac.tukorea.ge.spgp2026.taptu.game.scene.main.MainScene


// NoteSprite 는 Note data 하나를 화면에 보이는 Sprite 하나로 바꾼다.
// note 가 가진 time 과 현재 음악 시간의 차이를 y 좌표로 변환해,
// 음악이 진행될수록 goal line 을 향해 아래로 내려오게 한다.
class NoteSprite private constructor(
    gctx: GameContext,
    private val musicTimeProvider: () -> Float,
) : AnimSprite(gctx, R.mipmap.note, FPS, FRAME_COUNT), IRecyclable {
    private lateinit var note: Note

    init {
        setSize(WIDTH, HEIGHT)

        val musicTime = musicTimeProvider()
        createdOn = System.currentTimeMillis() - (musicTime * 1000).toLong()
    }

    // recycle bin 에서 다시 꺼낸 객체도 같은 init() 경로를 탄다.
    // 생성자는 bitmap/고정 크기/음악 시간 provider 를 준비하고,
    // note 별로 달라지는 값만 여기서 다시 채운다.
    fun init(note: Note): NoteSprite {
        this.note = note
        updatePosition()
        return this
    }

    override fun update(gctx: GameContext) {
        updatePosition()
        if (y > gctx.metrics.height + HEIGHT) {
            gctx.mainWorld().remove(this, MainLayer.NOTE)
        }
    }

    override fun onRecycle() {
        // NoteSprite 는 recycle bin 에 들어간 뒤 다른 Note 로 다시 init() 될 수 있다.
        // note 는 lateinit 이므로 null 로 비울 수는 없지만,
        // 다음에 recycle bin 에서 꺼낼 때 init(note) 로 반드시 새 Note 를 덮어쓴다.
        // 생성자는 private 이고 get() 이 매번 init(note) 를 호출하므로,
        // update/draw hot path 에서 isInitialized 같은 방어 분기를 둘 필요는 없다.
        //
        // musicTimeProvider 는 같은 MainScene/NoteGenerator 안에서 공유되는 시간 참조이므로
        // 객체 생성 시 한 번만 저장하고, recycle 될 때마다 다시 넣지 않는다.
    }

    private fun updatePosition() {
        val x = xFromPret(note.pret)
        val y = yFromTime(note.time, musicTimeProvider())
        setCenter(x, y)
    }

    companion object {
        private const val X_SPACE = 130f
        private const val LEFT = 450f - 2 * X_SPACE
        private const val WIDTH = 120f
        private const val HEIGHT = 55f
        const val GOAL_Y = 1400f

        // 이번 단계에서 가장 중요한 실험 상수이다.
        // note.time 과 musicTime 은 second 단위 Float 이고,
        // 두 값의 차이에 TIME_TO_Y 와 speed 를 곱한 만큼 GOAL_Y 위쪽에 배치한다.
        // 즉 TIME_TO_Y = 200f 라면 기본 배속에서 "음악 시간 1초 차이"가 화면에서는 "200 game unit 차이"로 보인다.
        const val TIME_TO_Y = 200f
        const val SPEED_NORMAL = 1.0f
        const val SPEED_FAST = 2.0f
        var speed = SPEED_NORMAL
        const val FPS = 16f
        const val FRAME_COUNT = 8

        fun get(gctx: GameContext, note: Note, musicTimeProvider: () -> Float): NoteSprite {
            val world = gctx.mainWorld()
            val noteSprite = world.obtain(NoteSprite::class.java) ?: NoteSprite(gctx, musicTimeProvider)
            return noteSprite.init(note)
        }

        fun screenfulTime(): Float {
            return (GOAL_Y + HEIGHT) / unitsPerSecond()
        }

        fun toggleSpeed(): Float {
            speed = if (speed == SPEED_FAST) SPEED_NORMAL else SPEED_FAST
            return speed
        }

        fun xFromPret(pret: Int): Float {
            // pret 0~4 는 5개의 lane 을 뜻한다.
            // 기본 가상 폭 900 에서 중앙 x=450 을 기준으로, 양쪽으로 X_SPACE 만큼 벌려
            // 190, 320, 450, 580, 710 위치에 note 를 놓는다.
            return LEFT + pret * X_SPACE
        }

        private fun yFromTime(noteTime: Float, musicTime: Float): Float {
            return GOAL_Y - (noteTime - musicTime) * unitsPerSecond()
        }

        private fun unitsPerSecond(): Float {
            return TIME_TO_Y * speed
        }
    }
}
