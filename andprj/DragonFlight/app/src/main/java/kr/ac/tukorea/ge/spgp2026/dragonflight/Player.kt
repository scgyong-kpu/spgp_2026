package kr.ac.tukorea.ge.spgp2026.dragonflight

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Gauge
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.abs

class Player(val gctx: GameContext) : Sprite(gctx, R.mipmap.fighters), IBoxCollidable {
    // 먼저 화면에 보이는 기본 크기와 시작 위치만 override 해 둔다.
    // 나중에 Player 가 화면 경계를 벗어나지 않게 하거나,
    // 기체별 크기를 다르게 둘 때도 같은 방식으로 값을 바꿀 수 있다.
    override var width = PLAYER_WIDTH
    override var height = PLAYER_HEIGHT
    override var x = gctx.metrics.width / 2f
    override var y = gctx.metrics.height - PLAYER_HEIGHT

    // x 는 Sprite 의 중심점이므로,
    // 좌우 경계도 이미지 폭의 절반만큼 안쪽에서 계산해야 한다.
    val minPlayerX = PLAYER_WIDTH / 2f
    val maxPlayerX = gctx.metrics.width - PLAYER_WIDTH / 2f

    // targetX 는 "플레이어가 현재 향하고 있는 목표 x 좌표"이다.
    // 터치가 들어오면 screen 좌표를 가상 좌표계로 변환한 뒤 이 값으로 기억해 둔다.
    // update() 는 매 프레임마다 현재 x 에서 targetX 쪽으로 조금씩 이동한다.
    private var targetX = x

    // targetX 를 화면에서 확인하기 쉽도록, 조이스틱 thumb 이미지를 임시 마커로 재사용한다.
    // Player 가 아직 target 위치에 도달하지 않은 동안만 이 위치를 그려 준다.
    private val targetBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.tu_joystick_thumb)
    private val targetRect = RectF()
    private val sparkBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.laser_spark)
    private val sparkRect = RectF()
    private var rollTime = 0f
    private var fireCoolTime = FIRE_INTERVAL
    // Enemy 는 여러 마리가 동시에 생기므로 Gauge 를 companion object 로 공유했지만,
    // Player 는 화면에 한 대만 존재하므로 굳이 static 처럼 공유할 필요가 없다.
    // 그래서 Player cooltime gauge 는 인스턴스 멤버로 두고, 이 Player 생성 시 바로 준비한다.
    private val cooltimeGauge = Gauge(
        0.1f,
        ContextCompat.getColor(gctx.view.context, R.color.player_gauge_fg),
        ContextCompat.getColor(gctx.view.context, R.color.player_gauge_bg),
    )

    override val collisionRect = RectF()

    init {
        // 값이 고정되어 있으니 Kotlin 스타일만 보면 property override 쪽이 더 자연스러워 보일 수 있다.
        // 하지만 지금 Sprite.srcRect 는 open 프로퍼티가 아니므로 여기서는 override 할 수 없다.
        // 그래서 현재 단계에서는 init 에서 한 번 설정해 두는 가장 단순한 방식을 사용한다.
        // fighters.png 는 80x80 frame 이 11장 가로로 붙어 있는 sprite sheet 이다.
        // Sprite 는 원래 bitmap 전체를 그리지만,
        // roll 단계에서는 srcRect 를 사용해 현재 기울기 frame 하나만 골라 그린다.
        srcRect = Rect(0, 0, PLANE_SRC_WIDTH, PLANE_SRC_WIDTH)

        // draw() 에서 더 이상 자동 sync 하지 않으므로,
        // 직접 지정한 초기 위치와 크기에 맞춰 dstRect 를 한 번 맞춰 둔다.
        syncDstRect()
        updateCollisionRect()
    }

    override fun update(gctx: GameContext) {
        // 이번 단계에서는 "방향"보다 "목표 위치"를 먼저 저장해 두고,
        // 그 목표를 향해 일정 속도로 가까워지는 방식으로 움직인다.
        //
        // step 은 이번 프레임에 최대로 이동할 수 있는 거리이다.
        // frameTime 을 곱해 두면 기기 성능이 달라도 1초 기준 속도는 비슷하게 유지된다.
        val step = SPEED * gctx.frameTime
        val delta = targetX - x

        // 남은 거리가 이번 프레임 이동 가능 거리보다 작으면
        // 지나쳐 버리지 않도록 targetX 에 정확히 맞춘다.
        x = when {
            delta > step -> x + step
            delta < -step -> x - step
            else -> targetX
        }

        // 좌우 경계는 미리 계산해 둔 minPlayerX, maxPlayerX 범위 안으로 다시 맞춘다.
        // 지금은 화면 폭(gctx.metrics.width)과 플레이어 폭을 이용해 경계를 계산하므로,
        // 해상도나 가상 좌표계 폭이 달라져도 같은 방식으로 동작한다.
        x = x.coerceIn(minPlayerX, maxPlayerX)

        // Player 는 update() 에서 x 를 직접 바꾸고,
        // a2dg Sprite 는 draw() 에서 더 이상 자동 syncDstRect() 를 하지 않는다.
        // 그래서 이동이 끝난 현재 위치를 기준으로 dstRect 를 여기서 다시 맞춰 둔다.
        syncDstRect()
        updateCollisionRect()

        fireBullet(gctx)
        updateRoll(gctx)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // fireCoolTime 은 발사 직후 FIRE_INTERVAL 로 되돌아갔다가 0 쪽으로 감소한다.
        // Gauge 에는 "얼마나 다시 발사 준비가 되었는지"를 보여 주고 싶으므로
        // 남은 시간 비율이 아니라 준비된 비율 1 - fireCoolTime / FIRE_INTERVAL 을 넘긴다.
        val cooltimeProgress = 1f - (fireCoolTime / FIRE_INTERVAL).coerceIn(0f, 1f)
        val gaugeWidth = width * 0.7f
        val gaugeX = x - gaugeWidth / 2f
        // Player cooltime gauge 는 기체 위쪽이 아니라 중심 y 를 기준으로 조금 아래에 둔다.
        // 이번 단계에서는 y + 70f 위치에 그려서 기체와 너무 겹치지 않게 본다.
        val gaugeY = y + 70f
        cooltimeGauge.draw(canvas, gaugeX, gaugeY, gaugeWidth, cooltimeProgress)

        // 총알을 막 발사한 직후의 아주 짧은 시간 동안만 스파크를 그린다.
        // 별도 Spark 오브젝트를 만들지 않고 Player 가 직접 그리면,
        // 발사 효과를 가장 작은 단계로 먼저 확인할 수 있다.
        if (FIRE_INTERVAL - fireCoolTime < SPARK_DURATION) {
            sparkRect.set(
                x - SPARK_WIDTH / 2f,
                y - SPARK_OFFSET - SPARK_HEIGHT / 2f,
                x + SPARK_WIDTH / 2f,
                y - SPARK_OFFSET + SPARK_HEIGHT / 2f,
            )
            canvas.drawBitmap(sparkBitmap, null, sparkRect, null)
        }

        // 이미 targetX 에 거의 도달했다면 target 마커는 굳이 그리지 않는다.
        if (abs(targetX - x) < 0.5f) return

        // Player 가 향하고 있는 목표 x 위치를 시각적으로 확인하기 위한 임시 표시이다.
        // y 는 Player 와 같은 높이에 두고, thumb 이미지를 작은 마커처럼 그린다.
        targetRect.set(
            targetX - TARGET_MARKER_SIZE / 2f,
            y - TARGET_MARKER_SIZE / 2f,
            targetX + TARGET_MARKER_SIZE / 2f,
            y + TARGET_MARKER_SIZE / 2f,
        )
        canvas.drawBitmap(targetBitmap, null, targetRect, null)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        // 이번 단계에서는 터치한 "화면 위치"가 아니라
        // 플레이어가 이동해야 할 "가상 좌표계 안의 목표 위치"가 중요하다.
        // 그래서 screen 좌표를 그대로 비교하지 않고,
        // metrics.fromScreen() 으로 virtual x 로 변환한 뒤 targetX 에 저장한다.
        val pt = gctx.metrics.fromScreen(event.x, event.y)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                targetX = pt.x.coerceIn(minPlayerX, maxPlayerX)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {}
        }
        return true
    }

    private fun fireBullet(gctx: GameContext) {
        fireCoolTime -= gctx.frameTime
        if (fireCoolTime > 0f) return

        // 발사 시점이 되었으면 다음 발사까지의 남은 시간을 다시 FIRE_INTERVAL 로 되돌린다.
        // 이렇게 하면 "이번 프레임에서 발사했다"는 사실만 기준으로 다음 쿨타임을 단순하게 다시 시작한다.
        fireCoolTime = FIRE_INTERVAL

        // 반대로 fireCoolTime += FIRE_INTERVAL 로 쓰는 방식은
        // "지금 프레임에서 얼마나 초과해서 늦었는지"를 일부 보존하고 다음 주기에 넘기는 방식이다.
        // 예를 들어 frameTime 이 커서 fireCoolTime 이 -0.03f 까지 내려갔다면,
        // += 는 그 0.03 초를 다음 쿨타임 계산에 반영하고,
        // = 는 그 초과분을 버리고 항상 FIRE_INTERVAL 부터 다시 시작한다.

        // 즉, FIRE_INTERVAL 간격 이내에는 발사를 안 하겠다는 건지
        // 100 * FIRE_INTERVAL 시간 동안 100번 발사를 꼭 하겠다는 건지
        // 선택을 하면 되는 문제이다.
        //
        // 현재 단계에서는 코드가 더 읽기 쉬운 = FIRE_INTERVAL 방식을 먼저 사용한다.
        // fireCoolTime += FIRE_INTERVAL

        val scene = gctx.scene as? MainScene ?: return
        // score 가 올라갈수록 총알 위력도 조금씩 강해지게 한다.
        // 현재 규칙은 score 0 일 때 power 10, 이후 1000 점마다 1씩 증가이다.
        val power = 10 + scene.getScore() / 1000

        // 총알이 기체 중심에서 바로 나오면 몸체와 겹쳐 보여 어색하다.
        // 그래서 이번 단계에서는 y 를 조금 위로 올린 위치에서 시작하게 해,
        // 전투기 앞쪽에서 발사되는 느낌이 나도록 보정한다.
        val bullet = Bullet.get(gctx, x, y - BULLET_OFFSET, power)
        scene.world.add(bullet, MainScene.Layer.BULLET)
    }

    private fun updateRoll(gctx: GameContext) {
        // targetX 와 현재 x 의 관계를 보고 어느 쪽으로 기울어야 하는지 부호를 정한다.
        var sign = when {
            targetX < x -> -1
            x < targetX -> 1
            else -> 0
        }

        // 비행기가 목표 위치에 도착해 멈췄다면,
        // 현재 남아 있는 기울기를 반대 방향으로 조금씩 줄여 0 으로 복귀시킨다.
        if (x == targetX) {
            if (rollTime > 0f) sign = -1
            else if (rollTime < 0f) sign = 1
        }

        rollTime += sign * gctx.frameTime

        // 0 으로 복귀하는 도중 반대편으로 지나치면 0 에 맞춘다.
        if (x == targetX) {
            if (sign < 0 && rollTime < 0f) rollTime = 0f
            if (sign > 0 && rollTime > 0f) rollTime = 0f
        }

        rollTime = rollTime.coerceIn(-MAX_ROLL_TIME, MAX_ROLL_TIME)

        // fighters.png 는 왼쪽 기울기 5장, 정면 1장, 오른쪽 기울기 5장으로 되어 있다.
        // 그래서 rollTime 범위를 -MAX_ROLL_TIME ~ +MAX_ROLL_TIME 로 맞춘 뒤
        // 현재 값을 0~10 frame index 로 바꿔 srcRect 에 반영한다.
        val rollIndex = 5 + (rollTime * 5 / MAX_ROLL_TIME).toInt()
        srcRect?.set(
            rollIndex * PLANE_SRC_WIDTH,
            0,
            (rollIndex + 1) * PLANE_SRC_WIDTH,
            PLANE_SRC_WIDTH,
        )
    }

    private fun updateCollisionRect() {
        // Player 는 날개까지 모두 맞은 것으로 보지 않도록 충돌 범위를 조금 줄여 둔다.
        // 좌우는 40, 상하는 20 만큼 안쪽으로 줄이면 보이는 그림보다 충돌 판정이 조금 보수적이 된다.
        collisionRect.set(dstRect)
        collisionRect.inset(COLLISION_INSET_X, COLLISION_INSET_Y)
    }

    companion object {
        const val SPEED = 300f
        const val PLAYER_WIDTH = 160f
        const val PLAYER_HEIGHT = 160f
        const val TARGET_MARKER_SIZE = 72f
        const val PLANE_SRC_WIDTH = 80
        const val MAX_ROLL_TIME = 0.4f
        const val FIRE_INTERVAL = 0.25f
        const val BULLET_OFFSET = 80f
        const val SPARK_OFFSET = 52f
        const val SPARK_DURATION = 0.1f
        const val SPARK_WIDTH = 94f
        const val SPARK_HEIGHT = SPARK_WIDTH * 3 / 5
        const val COLLISION_INSET_X = 40f
        const val COLLISION_INSET_Y = 20f
    }
}
