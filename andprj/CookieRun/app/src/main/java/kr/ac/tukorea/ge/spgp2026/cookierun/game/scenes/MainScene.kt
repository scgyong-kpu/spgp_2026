package kr.ac.tukorea.ge.spgp2026.cookierun.game.scenes

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Button
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.HorzScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R
import kr.ac.tukorea.ge.spgp2026.cookierun.game.layers.MainLayer
import kr.ac.tukorea.ge.spgp2026.cookierun.game.objs.common.IPausable
import kr.ac.tukorea.ge.spgp2026.cookierun.game.objs.controller.CollisionChecker
import kr.ac.tukorea.ge.spgp2026.cookierun.game.objs.map.MapLoader
import kr.ac.tukorea.ge.spgp2026.cookierun.game.objs.map.MapObjectCatalog
import kr.ac.tukorea.ge.spgp2026.cookierun.game.objs.player.Player

class MainScene(gctx: GameContext, private val stage: Int, cookieId: Int) : Scene(gctx) {
    companion object {
        private const val BUTTON_WIDTH = 200f
        private const val BUTTON_HEIGHT = 75f
        private const val PAUSE_BUTTON_SIZE = 100f
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
    val player = Player(gctx, cookieId)

    // World 는 레이어 순서대로 그려진다.
    // 여기서는 BG -> FLOOR -> PLAYER 순서이므로
    // 배경 뒤에 바닥이 깔리고 그 위에 플레이어가 올라오는 구성이 된다.
    override val world = World(MainLayer.entries.toTypedArray()).apply {
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
            add(HorzScrollBackground(gctx, resId, speed), MainLayer.BG)
        }
        // a to b 는 Pair(a, b) 와 같다. to 연산자 덕분에 가독성이 좋아진다.

        // CONTROLLER 레이어 안에서는 update() 를 역순으로 돌기 때문에,
        // CollisionChecker 를 먼저 추가하고 MapLoader 를 뒤에 추가해야
        // 매 프레임 item 생성이 먼저 일어나고, 그 다음 충돌 판정을 검사할 수 있다.
        add(CollisionChecker(this, player), MainLayer.CONTROLLER)
        add(MapLoader(gctx, this, stage), MainLayer.CONTROLLER)

        // 플레이어는 배경보다 앞 레이어에 배치한다.
        add(player, MainLayer.PLAYER)

        // 버튼도 화면에 그려져야 하므로 World 의 TOUCH layer 에 넣는다.
        // 동시에 MainScene.touchObjects() 가 같은 layer 를 Scene touch dispatch 대상으로 돌려준다.
        add(Button(gctx, R.mipmap.btn_slide_n, 150f, 800f, BUTTON_WIDTH, BUTTON_HEIGHT) { pressed ->
            player.slide(pressed)
            true
        }, MainLayer.TOUCH)
        add(Button(gctx, R.mipmap.btn_jump_n, 1450f, 770f, BUTTON_WIDTH, BUTTON_HEIGHT) { pressed ->
            if (pressed) {
                player.jump()
            }
            false
        }, MainLayer.TOUCH)
        add(Button(gctx, R.mipmap.btn_fall_n, 1450f, 850f, BUTTON_WIDTH, BUTTON_HEIGHT) { pressed ->
            if (pressed) {
                player.fall()
            }
            false
        }, MainLayer.TOUCH)
        add(Button(gctx, R.mipmap.btn_pause, 1500f, 100f, PAUSE_BUTTON_SIZE, PAUSE_BUTTON_SIZE) { pressed ->
            if (pressed) {
                PauseScene(gctx).push()
            }
            false
        }, MainLayer.TOUCH)
    }

    override fun touchObjects(): List<IGameObject> {
        return world.objectsAt(MainLayer.TOUCH)
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
        // ValueAnimator 처럼 World.update() 와 별도로 움직이는 객체는
        // IPausable 역할을 통해 Scene pause/resume 에 맞춰 직접 멈춘다.
        pausePausableObjects()
    }

    override fun onResume() {
        // pauseMusic() 으로 멈춘 배경음을 이어서 재생한다.
        gctx.res.sound.resumeMusic()
        // PauseScene 이 pop 되거나 Activity 가 foreground 로 돌아오면,
        // pause 해 둔 외부 작업도 같은 위치에서 이어서 재생한다.
        resumePausableObjects()
    }

    private fun pausePausableObjects() {
        world.forEachReversedAt(MainLayer.OBSTACLE) { obj ->
            (obj as? IPausable)?.pause()
        }
    }

    private fun resumePausableObjects() {
        world.forEachReversedAt(MainLayer.OBSTACLE) { obj ->
            (obj as? IPausable)?.resume()
        }
    }

    override fun onBackPressed(): Boolean {
        PauseScene(gctx).push()
        return true
    }
}
