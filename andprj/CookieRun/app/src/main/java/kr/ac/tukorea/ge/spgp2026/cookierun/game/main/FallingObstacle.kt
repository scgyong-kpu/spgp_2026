package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

// FallingObstacle 은 stage 파일의 'W' 문자로 생성되는 낙하형 장애물이다.
// Obstacle 의 공통 배치 로직으로 최종 위치를 먼저 잡은 뒤,
// Android ValueAnimator 를 이용해 위쪽에서 떨어지는 움직임을 만든다.
//
// SimpleObstacle 과 마찬가지로 이미지 한 장을 쓰지만,
// 별도 클래스로 분리해 두면 나중에 낙하 애니메이션, 충돌 박스 보정,
// pause/resume 처리 등을 다른 장애물과 섞지 않고 추가할 수 있다.
class FallingObstacle(gctx: GameContext): Obstacle(gctx, R.mipmap.epn01_tm01_sda) {
    private var animator: ValueAnimator? = null

    override fun init(left: Float, top: Float, width: Float) {
        // 먼저 Obstacle 의 공통 규칙으로 "최종적으로 놓일 위치"를 계산한다.
        // 여기서 만들어진 dstRect 는 낙하가 끝났을 때의 사각형이다.
        super.init(left, top, width)

        // 슬라이드로 피할 공간을 남기기 위해 최종 위치를 tile 바닥보다 조금 위로 올린다.
        // old 코드의 end = dstRect.top - 100 과 같은 의미다.
        val endTop = dstRect.top - SLIDE_CLEARANCE

        // 시작 위치는 장애물 자신의 높이만큼 더 위로 올린 곳이다.
        // 화면에는 위에서 내려오는 것처럼 보인다.
        dstRect.offset(0f, -dstRect.height())
        val startTop = dstRect.top

        startFallingAnimation(startTop, endTop)
    }

    private fun startFallingAnimation(startTop: Float, endTop: Float) {
        val animator = animator ?: ValueAnimator().apply {
            duration = FALL_DURATION_MILLIS
            startDelay = FALL_START_DELAY_MILLIS
            // Interpolator 는 시간 진행률(0.0~1.0)을 실제 움직임 진행률로 바꿔 주는 함수이다.
            // 같은 startTop/endTop 을 쓰더라도 어떤 Interpolator 를 쓰느냐에 따라
            // 떨어지는 느낌이 완전히 달라진다.
            //
            // 수업 시간에는 아래 줄을 하나씩 풀어서 움직임 차이를 확인해 볼 수 있다.
//            interpolator = LinearInterpolator() // 일정한 속도로 딱딱하게 이동한다.
//            interpolator = AccelerateInterpolator() // 처음에는 느리다가 점점 빨라져 중력 느낌이 난다.
//            interpolator = DecelerateInterpolator() // 처음에는 빠르다가 끝에서 부드럽게 멈춘다.
//            interpolator = AccelerateDecelerateInterpolator() // 천천히 시작해서 빨라졌다가 다시 천천히 멈춘다.
//            interpolator = OvershootInterpolator() // 목표 지점을 살짝 지나쳤다가 되돌아온다.
//            interpolator = AnticipateInterpolator() // 반대 방향으로 살짝 움찔한 뒤 출발한다.
//            interpolator = AnticipateOvershootInterpolator() // 움찔했다가 지나치고 되돌아오는 과장된 움직임이다.
//            interpolator = BounceInterpolator() // 바닥에 닿은 뒤 통통 튀는 느낌이다.
            addUpdateListener { animation ->
                val top = animation.animatedValue as Float
                // MapObject.update() 는 매 프레임 dstRect 를 왼쪽으로 이동시킨다.
                // Animator 는 y 위치만 담당해야 하므로, 현재 left 는 그대로 두고 top 만 바꾼다.
                dstRect.offsetTo(dstRect.left, top)
            }
            this@FallingObstacle.animator = this
        }

        // 재활용 객체라면 이전 animator 가 아직 끝나지 않았을 수 있다.
        // 새 시작/끝 값을 넣기 전에 멈춰 두어, 이전 애니메이션 값이 섞이지 않게 한다.
        animator.cancel()
        animator.setFloatValues(startTop, endTop)
        animator.start()
    }

    override fun onRecycle() {
        super.onRecycle()
        // 화면에서 사라진 객체는 World.update()/draw() 대상에서는 빠지지만,
        // ValueAnimator 는 Android framework 쪽에서 별도로 실행된다.
        // 따라서 recycle bin 에 들어가기 직전에 cancel() 해서
        // 더 이상 필요 없는 callback 을 끊어야 한다.
        animator?.cancel()
    }

    companion object {
        // MapObjectCatalog 에 등록된 'W' 생성 규칙은 이 get() 을 통해 장애물을 얻는다.
        // World 에 재활용 가능한 객체가 있으면 새로 만들지 않고 다시 초기화해서 사용한다.
        fun get(gctx: GameContext, left: Float, top: Float): Obstacle {
            val world = (gctx.scene as MainScene).world
            // World 에서 재활용 가능한 FallingObstacle 이 있는지 찾아본다.
            // 낙하 장애물은 이후 상태값이 추가될 수 있으므로,
            // SimpleObstacle 과 recycle bin 이 섞이지 않도록 구체 클래스로 obtain 한다.
            val obs = world.obtain(FallingObstacle::class.java) ?: FallingObstacle(gctx)
            obs.init(left, top, WIDTH)
            return obs
        }
        // stage tile 하나가 100f 폭이므로, 원본 이미지 비율을 유지하면서
        // tile 보다 살짝 넓게 보이도록 108f 를 기준 폭으로 잡는다.
        private const val WIDTH = 108f
        private const val SLIDE_CLEARANCE = 100f
        private const val FALL_DURATION_MILLIS = 2_000L
        private const val FALL_START_DELAY_MILLIS = 1_000L
    }
}
