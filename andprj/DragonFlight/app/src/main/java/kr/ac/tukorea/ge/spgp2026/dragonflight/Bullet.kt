package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Bullet private constructor(
    gctx: GameContext,
) : Sprite(gctx, R.mipmap.laser_1), IBoxCollidable, IRecyclable {
    override var width = BULLET_WIDTH
    override var height = BULLET_HEIGHT
    override var x = 0f
    override var y = 0f
    var power = 0

    init {
        // draw() 에서 자동 sync 를 하지 않으므로,
        // 생성 시 직접 넣은 초기 위치/크기에 맞춰 dstRect 를 한 번 맞춰 둔다.
        syncDstRect()
    }

    // 재활용된 Bullet 이든 새로 만든 Bullet 이든, 실제 발사에 필요한 값은 여기서 다시 채운다.
    // 지금 구조에서는 World.obtain(Bullet::class.java) 가 bin 에서 하나를 꺼내거나 null 을 돌려주고,
    // Bullet.get(...) 이 null 이면 Bullet(gctx) 로 새로 만든 뒤 init(...) 을 호출한다.
    fun init(startX: Float, startY: Float, power: Int): Bullet {
        x = startX
        y = startY
        this.power = power
        syncDstRect()
        return this
    }

    // 지금 단계의 충돌 범위는 draw 에 쓰는 목적 사각형과 같은 값으로 본다.
    // 즉 Bullet 은 별도 collision box 를 따로 계산하지 않고,
    // 현재 화면에 그려질 영역(dstRect)을 그대로 collisionRect 로 사용한다.
    override val collisionRect: RectF
        get() = dstRect

    override fun update(gctx: GameContext) {
        // 현재 Bullet 은 x 는 그대로 두고 y 만 감소시키며 위쪽으로 직진한다.
        y -= SPEED * gctx.frameTime
        // Bullet 도 update() 에서 y 를 직접 바꾸므로,
        // draw 나 collisionRect getter 에 맡기지 말고 여기서 dstRect 를 최신 위치로 맞춘다.
        syncDstRect()

        // 총알이 화면 위를 완전히 벗어나면 현재 Scene 의 BULLET layer 에서 제거한다.
        if (y + height / 2f < 0f) {
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.BULLET)
        }
    }

    override fun onRecycle() {
    }

    companion object {
        const val BULLET_WIDTH = 68f
        const val BULLET_HEIGHT = BULLET_WIDTH * 40f / 28f
        const val SPEED = 2000f

        // Bullet 은 이제 바깥에서 Bullet(...) 생성자를 직접 호출하지 않게 하고,
        // 반드시 Bullet.get(...) 으로만 얻도록 규칙을 모은다.
        //
        // 이렇게 하면 호출하는 쪽(Player)은:
        // - 재활용 객체를 꺼냈는지
        // - 새로 만들었는지
        // - init(...) 을 어떻게 부르는지
        // 를 알 필요가 없다.
        //
        // 이번 단계의 재활용 규칙은
        // 1. world.obtain(Bullet::class.java) 로 recycle bin 을 먼저 본다.
        // 2. 없으면 이 companion object 안에서만 Bullet(gctx) 로 새로 만든다.
        // 3. 마지막에 init(...) 으로 실제 발사 위치와 power 를 다시 채운다.
        //
        // 생성자를 private 으로 둔 이유는 "Bullet 은 get(...) 을 통해서만 얻는다"는 규칙을
        // 코드 차원에서 강제하기 위해서이다.
        fun get(gctx: GameContext, x: Float, y: Float, power: Int): Bullet {
            val scene = gctx.scene as? MainScene ?: return Bullet(gctx).init(x, y, power)
            val bullet = scene.world.obtain(Bullet::class.java) ?: Bullet(gctx)
            return bullet.init(x, y, power)
        }
    }
}
