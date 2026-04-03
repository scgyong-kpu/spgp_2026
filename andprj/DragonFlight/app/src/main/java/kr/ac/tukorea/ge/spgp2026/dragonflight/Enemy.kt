package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.content.ContextCompat
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Gauge
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Enemy private constructor(
    private val gctx: GameContext,
) : AnimSprite(gctx, RES_IDS[0], FPS), IBoxCollidable, IRecyclable {
    var level = 1
        private set
    var life = level * LIFE_PER_LEVEL
        private set
    var maxLife = life
        private set
    val dead: Boolean
        get() = life <= 0
    override val collisionRect = RectF()

    // 리소스 이름이 enemy_01, enemy_02 ... enemy_20 처럼 1부터 시작하므로,
    // 수업 코드에서도 level 을 0-base 대신 1-base 로 다루는 편이 더 자연스럽다.
    // 그래서 Enemy 생성자는 level = 1 이 첫 번째 적 이미지를 뜻하게 하고,
    // 실제 배열 접근에서만 [level - 1] 로 바꿔 준다.
    // Enemy 는 생성자가 넘겨 준 x 위치를 기준으로 화면 위에서 시작한다.
    // 지금은 EnemyGenerator 가 각 적의 x 를 계산해 넘겨 주고 있으므로,
    // Enemy 자신은 "어느 x 에 배치할지"보다 "화면 위에서 어떻게 내려올지"에만 집중하면 된다.
    // y 시작 위치는 "적은 위에서 내려온다"는 규칙으로 정해져 있으므로
    // 생성자에서 매번 넘기기보다 Enemy 안에서 고정하는 편이 더 읽기 쉽다.
    //
    // x, y 는 Sprite 의 중심점이다.
    // 그래서 y 를 ENEMY_HEIGHT / 2f 로 두면 화면 위에 "딱 붙어" 시작하고,
    // y 를 -ENEMY_HEIGHT / 2f 로 두면 이미지 전체가 화면 밖에 있는 상태에서 내려오게 된다.
    // DragonFlight 류 게임에서는 적이 화면 위 바깥에서 진입하는 느낌이 더 자연스러우므로
    // 여기서는 후자를 사용한다.
    override var width = ENEMY_WIDTH
    override var height = ENEMY_HEIGHT
    override var x = 0f
    override var y = -ENEMY_HEIGHT / 2f
    private var speed = DEFAULT_SPEED

    init {
        // Gauge 는 Enemy 마다 새로 만들지 않고,
        // 첫 Enemy 가 생성될 때 한 번만 만들어 이후 Enemy 들이 함께 재사용한다.
        if (gauge == null) {
            gauge = Gauge(
                0.1f,
                ContextCompat.getColor(gctx.view.context, R.color.enemy_gauge_fg),
                ContextCompat.getColor(gctx.view.context, R.color.enemy_gauge_bg),
            )
        }

        // Enemy 도 생성 시 직접 위치와 크기를 대입하므로,
        // 첫 draw 전에 dstRect 와 collisionRect 를 미리 맞춰 둔다.
        // 체력도 level 에 따라 다르게 시작한다.
        // 현재 규칙은 level 1 이 10, level 20 이 200 이 되도록
        // level * LIFE_PER_LEVEL 공식을 그대로 사용한다.
        syncDstRect()
        updateCollisionRect()
    }

    // Enemy 는 Bullet 보다 재초기화할 값이 더 많다.
    // 재활용 객체를 다시 꺼냈을 때는:
    // - image strip
    // - level
    // - speed
    // - life / maxLife
    // - 위치와 dstRect / collisionRect
    // 를 모두 현재 wave 조건에 맞게 다시 세팅해야 한다.
    fun init(x: Float, level: Int, speed: Float): Enemy {
        this.level = level
        this.speed = speed
        this.life = level * LIFE_PER_LEVEL
        this.maxLife = life
        // 현재 a2dg AnimSprite 에는 setImageResourceId() 같은 helper 가 아직 없으므로,
        // 재활용된 Enemy 는 여기서 bitmap 만 직접 바꾼다.
        // Enemy strip 들은 fps 와 frame 구조가 모두 같으므로, 생성자에서 정한 애니메이션 설정을 그대로 써도 된다.
        bitmap = gctx.res.getBitmap(RES_IDS[level - 1])
        this.x = x
        this.y = -ENEMY_HEIGHT / 2f
        syncDstRect()
        updateCollisionRect()
        return this
    }

    // Enemy 의 collisionRect 는 그림에 쓰는 dstRect 와 완전히 같지 않다.
    // 현재 단계에서는 dstRect 를 복사한 뒤 사방을 11f 만큼 안쪽으로 줄여,
    // 눈에 보이는 날개/여백보다 조금 안쪽에서 충돌이 나도록 조정한다.
    override fun update(gctx: GameContext) {
        // 아래쪽으로 움직일 때도 중심점 y 를 더한다.
        // 삭제 조건은
        //   y - height / 2f > gctx.metrics.height
        // 처럼 "적의 윗변이 화면 아래를 지나갔는지"를 보면
        // 적이 완전히 안 보인 뒤에 제거할 수 있다.
        y += speed * gctx.frameTime

        if (y - height / 2f > gctx.metrics.height) {
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.ENEMY)
        }
        syncDstRect()
        updateCollisionRect()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Gauge 는 길이 1.0 기준선에 scale 을 곱해 그리므로,
        // 여기서는 적 폭의 70%를 실제 gauge 길이로 사용한다.
        // life / maxLife 를 넘기면 현재 체력 비율만큼만 앞쪽 선이 그려진다.
        val gaugeWidth = width * 0.7f
        val gaugeX = x - gaugeWidth / 2f
        val gaugeY = dstRect.bottom
        gauge?.draw(canvas, gaugeX, gaugeY, gaugeWidth, life.toFloat() / maxLife)
    }

    private fun updateCollisionRect() {
        collisionRect.set(dstRect)
        collisionRect.inset(COLLISION_INSET, COLLISION_INSET)
    }

    fun decreaseLife(power: Int) {
        life -= power
    }

    fun getScore(): Int {
        // 현재 Enemy level 은 1-base 이므로,
        // 점수도 level 1 -> 100, level 20 -> 2000 형태로 바로 읽히게 둔다.
        return level * SCORE_PER_LEVEL
    }

    override fun onRecycle() {
    }

    companion object {
        const val ENEMY_WIDTH = 180f
        const val ENEMY_HEIGHT = 180f
        const val DEFAULT_SPEED = 240f
        const val FPS = 10f
        const val MAX_LEVEL_COUNT = 20
        const val COLLISION_INSET = 11f
        const val LIFE_PER_LEVEL = 10
        const val SCORE_PER_LEVEL = 100
        private var gauge: Gauge? = null
        private val RES_IDS = intArrayOf(
            R.mipmap.enemy_01, R.mipmap.enemy_02, R.mipmap.enemy_03, R.mipmap.enemy_04, R.mipmap.enemy_05,
            R.mipmap.enemy_06, R.mipmap.enemy_07, R.mipmap.enemy_08, R.mipmap.enemy_09, R.mipmap.enemy_10,
            R.mipmap.enemy_11, R.mipmap.enemy_12, R.mipmap.enemy_13, R.mipmap.enemy_14, R.mipmap.enemy_15,
            R.mipmap.enemy_16, R.mipmap.enemy_17, R.mipmap.enemy_18, R.mipmap.enemy_19, R.mipmap.enemy_20,
        )

        // Enemy 도 Bullet 과 같은 재활용 패턴을 따른다.
        // 호출하는 쪽은 Enemy 가 재활용되었는지 새로 만들어졌는지 신경 쓰지 않고,
        // Enemy.get(...) 이 world.obtain(...) ?: Enemy(gctx) 로 내부에서 처리한다.
        fun get(gctx: GameContext, x: Float, level: Int, speed: Float): Enemy {
            val scene = gctx.scene as? MainScene ?: return Enemy(gctx).init(x, level, speed)
            val enemy = scene.world.obtain(Enemy::class.java) ?: Enemy(gctx)
            return enemy.init(x, level, speed)
        }
    }
}
