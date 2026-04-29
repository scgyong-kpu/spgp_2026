package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.BounceInterpolator
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
        Log.d(
            javaClass.simpleName,
            "init obj=${System.identityHashCode(this)} left=$left top=$top",
        )

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
//            interpolator = BounceInterpolator()
            addUpdateListener { animation ->
                val top = animation.animatedValue as Float
                Log.d(
                    javaClass.simpleName,
                    "anim update obj=${System.identityHashCode(this)} top=$top left=${dstRect.left}",
                )
                // MapObject.update() 는 매 프레임 dstRect 를 왼쪽으로 이동시킨다.
                // Animator 는 y 위치만 담당해야 하므로, 현재 left 는 그대로 두고 top 만 바꾼다.
                dstRect.offsetTo(dstRect.left, top)
            }
            this@FallingObstacle.animator = this
        }

        // 실험 1단계에서는 이 cancel() 도 일부러 막아 둔다.
        // 지금 구조는 같은 ValueAnimator 인스턴스를 재사용하므로,
        // 이 줄을 막아도 화면상 오동작이 바로 보이지 않을 수 있다.
        //
        // 여기서 확인하려는 핵심은 "오동작"이 아니라,
        // 화면 밖으로 사라진 객체의 Animator callback 이 계속 살아 있는지 여부이다.
        // animator.cancel()
        animator.setFloatValues(startTop, endTop)
        animator.start()
    }

    // 실험 1단계: onRecycle() 을 일부러 막아 둔 상태.
    //
    // 화면에서 사라진 객체는 World.update()/draw() 대상에서는 빠진다.
    // 하지만 ValueAnimator 는 Android framework 쪽에서 별도로 실행되므로,
    // World 에서 remove 되었다고 자동으로 멈추지 않는다.
    //
    // 그래서 onRecycle() 을 막아 두면, FallingObstacle 이 화면 왼쪽 밖으로 사라진 뒤에도
    // anim update 로그가 계속 찍히는 것을 볼 수 있다.
    // 이것은 "보이지 않는 객체에 붙은 외부 작업이 아직 살아 있다"는 뜻이다.
    //
    // 이 예제에서 화면상 오동작이 꼭 보이지는 않을 수 있다.
    // 하지만 로그가 계속 찍히는 것만으로도,
    // GameObject 의 생명주기와 Android Animator 의 생명주기가 서로 다르다는 점을 확인할 수 있다.

    // 심지어 이 Animator 는 Activity 가 종료된 후에도 계속 진행되는데, 이 문제는 더 나중에
    // 다른 방식으로 해결할 예정이다

    //
    // 실험 2단계에서는 아래 onRecycle() 을 다시 켠다.
    // 정상 구현에서는 recycle bin 에 들어가기 직전에 animator.cancel() 을 호출해
    // 더 이상 필요 없는 callback 을 끊어야 한다.
    // 그러면 화면에서 사라진 뒤 anim update 로그도 멈추는 것을 확인할 수 있다.
    override fun onRecycle() {
        super.onRecycle()
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
        // 원래 값은 2초 정도면 충분하지만, 실험 1단계에서는 일부러 길게 둔다.
        // 그래야 장애물이 화면 밖으로 사라진 뒤에도 animator 로그가 계속 나오는 것을 관찰하기 쉽다.
//        private const val FALL_DURATION_MILLIS = 2_000L
        private const val FALL_START_DELAY_MILLIS = 0L
        private const val FALL_DURATION_MILLIS = 10_000L
    }
}
