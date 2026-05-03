package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Button
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.HorzScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R

class MainScene(gctx: GameContext, private val stage: Int) : Scene(gctx) {
    companion object {
        private const val BUTTON_WIDTH = 200f
        private const val BUTTON_HEIGHT = 75f
    }

    // 예전처럼 0, 1 같은 Int 로 레이어를 구분할 수도 있지만,
    // enum 을 쓰면 각 레이어의 의미가 이름으로 드러나서 읽기와 유지보수가 쉬워진다.
    enum class Layer {
        // OBSTACLE 은 FLOOR/ITEM 보다 앞에, PLAYER 보다 뒤에 둔다.
        // 이렇게 하면 장애물이 바닥과 아이템 위에 보이면서도,
        // 플레이어가 장애물에 가려지지 않아 충돌 상황을 확인하기 쉽다.
        // TOUCH 는 화면에 그려지는 버튼을 담는 레이어이다.
        // World 는 이 레이어를 draw 하고, Scene 은 같은 레이어를 touch dispatch 대상으로도 사용한다.
        BG, FLOOR, ITEM, OBSTACLE, PLAYER, TOUCH, CONTROLLER
    }

    // Scene 경계 바깥은 그리지 않도록 잘라서(drawing clip) 불필요한 오버드로우를 줄인다.
    override val clipsRect = true

    init {
        // MapLoader 는 stage 문자를 MapObjectRegistry 를 통해 객체로 바꾼다.
        // 따라서 MapLoader 가 생성되기 전에, 이 게임에서 사용할 문자별 생성 규칙을 등록해 둔다.
        // registerAll() 은 MainScene 이 다시 만들어져도 중복 등록되지 않도록 내부에서 한 번만 실행된다.
        MapObjectCatalog.registerAll()
    }

    // 플레이어를 멤버로 분리해 두면,
    // 이후 입력 처리나 카메라 추적에서 MainScene 이 직접 접근하기 쉽다.
    val player = Player(gctx)

    // World 는 레이어 순서대로 그려진다.
    // 여기서는 BG -> FLOOR -> PLAYER 순서이므로
    // 배경 뒤에 바닥이 깔리고 그 위에 플레이어가 올라오는 구성이 된다.
    override val world = World(Layer.entries.toTypedArray()).apply {
        // (배경 리소스, 스크롤 속도) 쌍을 한 번에 선언해 반복 추가한다.
        // speed 가 음수면 오른쪽에서 왼쪽으로 이동한다.
        // 앞쪽 레이어일수록 절댓값을 크게 주면 parallax(원근감) 효과가 난다.
        listOf(
            R.mipmap.cookie_run_bg_1 to -50f,
            R.mipmap.cookie_run_bg_2 to -100f,
            R.mipmap.cookie_run_bg_3 to -150f,
        ).forEach { (resId, speed) ->
            // 같은 코드 패턴으로 배경을 추가하므로 유지보수가 쉽다.
            // 배경 장수를 늘릴 때는 위 리스트에 항목만 추가하면 된다.
            add(HorzScrollBackground(gctx, resId, speed), Layer.BG)
        }
        // a to b 는 Pair(a, b) 와 같다. to 연산자 덕분에 가독성이 좋아진다.

        // CONTROLLER 레이어 안에서는 update() 를 역순으로 돌기 때문에,
        // CollisionChecker 를 먼저 추가하고 MapLoader 를 뒤에 추가해야
        // 매 프레임 item 생성이 먼저 일어나고, 그 다음 충돌 판정을 검사할 수 있다.
        add(CollisionChecker(this, player), Layer.CONTROLLER)
        add(MapLoader(gctx, this, stage), Layer.CONTROLLER)

        // 플레이어는 배경보다 앞 레이어에 배치한다.
        add(player, Layer.PLAYER)

        // 버튼도 화면에 그려져야 하므로 World 의 TOUCH layer 에 넣는다.
        // 동시에 MainScene.touchObjects() 가 같은 layer 를 Scene touch dispatch 대상으로 돌려준다.
        add(Button(gctx, R.mipmap.btn_slide_n, 150f, 800f, BUTTON_WIDTH, BUTTON_HEIGHT) { pressed ->
            player.slide(pressed)
            true
        }, Layer.TOUCH)
        add(Button(gctx, R.mipmap.btn_jump_n, 1450f, 770f, BUTTON_WIDTH, BUTTON_HEIGHT) { pressed ->
            if (pressed) {
                player.jump()
            }
            false
        }, Layer.TOUCH)
    }

    override fun touchObjects(): List<IGameObject> {
        return world.objectsAt(Layer.TOUCH)
    }

    override fun onEnter() {
        // MainScene 이 game stack 에 올라오면 배경음을 시작한다.
        // 효과음은 각 오브젝트가 필요한 순간 직접 재생하지만,
        // 배경음은 Scene 전체에 속한 상태라 Scene lifecycle 에 맞춰 다룬다.
        gctx.res.sound.playMusic(R.raw.main)
    }

    override fun onExit() {
        // MainScene 이 끝나면 MediaPlayer 도 정리한다.
        // stopMusic() 은 내부 MediaPlayer 를 release 하므로,
        // Scene 이 사라진 뒤에도 음악 리소스가 남아 있지 않게 한다.
        gctx.res.sound.stopMusic()
    }

    override fun onPause() {
        // Activity 가 background 로 가거나 PauseScene 이 올라오는 경우,
        // 배경음은 현재 위치를 유지한 채 잠시 멈춘다.
        gctx.res.sound.pauseMusic()
    }

    override fun onResume() {
        // pauseMusic() 으로 멈춘 배경음을 이어서 재생한다.
        gctx.res.sound.resumeMusic()
    }

}
