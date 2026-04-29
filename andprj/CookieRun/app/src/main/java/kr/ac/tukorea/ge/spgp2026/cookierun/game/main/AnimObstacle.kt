package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// AnimObstacle 은 stage 파일의 'Y', 'Z' 문자로 생성되는 애니메이션 장애물이다.
// 화면 오른쪽 바깥에 미리 만들어 두었다가,
// 플레이어가 볼 수 있을 만큼 가까워지면 프레임 애니메이션을 시작한다.
//
// 생성자에는 투명 이미지를 넘겨 두고, init() 에서 실제 type 이 정해진 뒤
// 위치와 크기를 계산할 때만 첫 프레임 bitmap 으로 교체한다.
class AnimObstacle(val gctx: GameContext) : Obstacle(gctx, R.mipmap.trans_00p) {
    private lateinit var type: Type
    private var time = 0f

    fun init(left: Float, top: Float, type: Type) {
        this.type = type
        time = 0f
        // Obstacle.init() 은 현재 bitmap 크기로 높이를 계산한다.
        // 따라서 공통 init() 을 호출하기 전에 type 에 맞는 첫 프레임 bitmap 을 먼저 넣어야 한다.
        bitmap = gctx.res.getBitmap(type.resIds[0])
        super.init(left, top, type.width)

        // 배치 크기를 계산한 뒤에는 다시 투명 bitmap 으로 바꿔 둔다.
        // 이렇게 하면 화면 오른쪽 멀리 있을 때부터 장애물이 보이지 않고,
        // update() 에서 시작 위치에 도달한 뒤에야 실제 프레임이 나타난다.
        bitmap = gctx.res.getBitmap(R.mipmap.trans_00p)
    }

    override fun update(gctx: GameContext) {
        super.update(gctx)
        if (dstRect.left >= START_ANIM_LEFT) {
            // 아직 플레이어 시야에 들어오기 전이면 시간도 누적하지 않는다.
            // 그래야 모든 AnimObstacle 이 화면에 가까워진 시점부터 같은 속도로 애니메이션된다.
            return
        }

        time += gctx.frameTime
        val frameIndex = (time * FPS).toInt().coerceAtMost(type.resIds.lastIndex)
        bitmap = gctx.res.getBitmap(type.resIds[frameIndex])
    }

    // Type 은 애니메이션 장애물의 종류를 구분한다.
    // resIds 는 시간에 따라 보여 줄 프레임 이미지 목록이고,
    // width 는 이 장애물이 게임 좌표계에서 차지할 기준 폭이다.
    enum class Type(
        val resIds: IntArray,
        val width: Float
    ) {
        SPIKY_3(intArrayOf(
            R.mipmap.epn01_tm01_jp1up_01,
            R.mipmap.epn01_tm01_jp1up_02,
            R.mipmap.epn01_tm01_jp1up_03,
            R.mipmap.epn01_tm01_jp1up_04,
        ), SPIKY3_WIDTH),
        SPIKY_2(intArrayOf(
            R.mipmap.epn01_tm01_jp2up_01,
            R.mipmap.epn01_tm01_jp2up_02,
            R.mipmap.epn01_tm01_jp2up_03,
            R.mipmap.epn01_tm01_jp2up_04,
            R.mipmap.epn01_tm01_jp2up_05,
        ), SPIKY2_WIDTH),
    }
    companion object {
        // MapObjectCatalog 에 등록된 'Y'/'Z' 생성 규칙은 이 get() 을 통해 장애물을 얻는다.
        // World 에 재활용 가능한 객체가 있으면 새로 만들지 않고 다시 초기화해서 사용한다.
        fun get(gctx: GameContext, type: AnimObstacle.Type, left: Float, top: Float): Obstacle {
            val world = (gctx.scene as MainScene).world
            // World 에서 재활용 가능한 AnimObstacle 이 있는지 찾아본다.
            // 같은 AnimObstacle 객체라도 init() 에서 Type 을 다시 받으므로
            // Y/Z 중 어느 문자에서 왔든 재사용할 수 있다.
            val obs = world.obtain(AnimObstacle::class.java) ?: AnimObstacle(gctx)
            obs.init(left, top, type)
            return obs
        }
        private const val FPS = 8f
        private const val START_ANIM_LEFT = 1000f
        private const val SPIKY3_WIDTH = 101f
        private const val SPIKY2_WIDTH = 109f
    }
}
