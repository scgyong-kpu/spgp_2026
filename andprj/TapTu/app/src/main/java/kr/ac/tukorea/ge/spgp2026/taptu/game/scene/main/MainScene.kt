package kr.ac.tukorea.ge.spgp2026.taptu.game.scene.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.util.Log
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Button
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.data.SongCatalog
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.taptu.game.objs.Call
import kr.ac.tukorea.ge.spgp2026.taptu.game.objs.NoteGenerator
import kr.ac.tukorea.ge.spgp2026.taptu.game.objs.NoteSprite
import kr.ac.tukorea.ge.spgp2026.taptu.game.objs.Pret
import kr.ac.tukorea.ge.spgp2026.taptu.game.objs.PretBg
import kr.ac.tukorea.ge.spgp2026.taptu.game.scene.pause.PauseScene
import kr.ac.tukorea.ge.spgp2026.taptu.res.BitmapBlur


class MainScene(
    gctx: GameContext,
    songIndex: Int,
) : Scene(gctx) {
    override val world = World<MainLayer>(MainLayer.entries.toTypedArray())
    val song = SongCatalog.songs[songIndex]
    private var mediaPlayer: MediaPlayer? = null
    private var finishFadeAnimator: ValueAnimator? = null
    private var finishFadeStarted = false
    val musicTime: Float
        get() = (mediaPlayer?.currentPosition ?: 0) / 1000f

    private lateinit var speedBtn: Button
    override fun onEnter() {
        val screenWidth = gctx.metrics.width
        val screenHeight = gctx.metrics.height
        val centerX = screenWidth / 2
        val centerY = screenHeight / 2

        val context = gctx.view.context
        song.loadNotes(context.assets)
        song.rewind()

        val thumbnail =
            song.loadThumbnail(context.assets) ?: gctx.res.getBitmap(R.mipmap.default_thumbnail)
        val blurredThumbnail = BitmapBlur.blurBitmap(context, thumbnail)
        val albumCover = Sprite(gctx, bitmap = blurredThumbnail, resId = R.mipmap.default_thumbnail)
        albumCover.setCenterProportionalHeight(
            centerX,
            centerY,
            screenHeight,
        )
        world.add(albumCover, MainLayer.BG)

        val bg = PretBg(gctx, song) { musicTime }
        world.add(bg, MainLayer.BG)

        for (index in 0 until 5) {
            world.add(Pret(gctx, index), MainLayer.PRET)
        }
        val backBtn: Button = Button(gctx, R.mipmap.go_back, 50f, 50f, 100f, 100f) { pressed ->
            pop()
            false
        }
        world.add(backBtn, MainLayer.UI)

        speedBtn =
            Button(gctx, R.mipmap.speed_1x, gctx.metrics.width - 50f, 50f, 100f, 100f) { pressed ->
                toggleSpeed()
                false
            }
        world.add(speedBtn, MainLayer.UI)

        world.add(
            NoteGenerator(
                song,
                world,
                musicTimeProvider = { musicTime },
                onFinished = { startFinishFadeOut() },
            ),
            MainLayer.CONTROLLER,
        )

        playMusic()
    }

    private fun toggleSpeed() {
        val speed: Float = NoteSprite.toggleSpeed()
        val mipmapId: Int =
            if (speed == NoteSprite.SPEED_NORMAL) R.mipmap.speed_1x else R.mipmap.speed_2x
        speedBtn.bitmap = gctx.res.getBitmap(mipmapId)
    }

    private fun startFinishFadeOut() {
        if (finishFadeStarted) return
        finishFadeStarted = true

        val halfScreenfulMillis = (NoteSprite.screenfulTime() * 500).toLong()
        val animator = ValueAnimator.ofFloat(1.0f, 0.0f).apply {
            // 마지막 note 가 지나간 뒤 screenfulTime() 전체를 기다리되,
            // 앞 절반은 그대로 재생하고 뒤 절반에서만 volume 을 1.0 -> 0.0 으로 낮춘다.
            startDelay = halfScreenfulMillis
            duration = halfScreenfulMillis
            addUpdateListener { animator ->
                val volume = animator.animatedValue as Float
                mediaPlayer?.setVolume(volume, volume)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // releaseMusic() 에서 cancel 되는 경우에는 finishFadeAnimator 를 먼저 null 로 만든다.
                    // 이 check 를 두면 Scene 이 이미 종료되는 중일 때 onAnimationEnd 가 뒤늦게 pop() 을 다시 호출하지 않는다.
                    if (finishFadeAnimator !== animation) return
                    // PauseScene 같은 다른 Scene 이 위에 올라와 있는 동안 animator callback 이 도착하면,
                    // pop() 은 MainScene 이 아니라 top 인 PauseScene 을 닫아 버린다.
                    // 그래서 현재 top 이 MainScene 자신일 때만 곡 종료 pop 을 수행한다.
                    if (gctx.sceneStack.top !== this@MainScene) return
                    finishFadeAnimator = null
                    pop()
                }
            })
        }
        finishFadeAnimator = animator
        animator.start()
    }

    override fun touchObjects(): List<IGameObject> {
        return world.objectsAt(MainLayer.UI)
    }

    var selectedPret = -1
    fun selectPret(pretIndex: Int) {
        selectedPret = pretIndex
        val prets = world.objectsAt(MainLayer.PRET)
        prets.forEachIndexed { index, obj ->
            val pret = obj as? Pret ?: return@forEachIndexed
            pret.shows = index == pretIndex
        }
//        Log.d(javaClass.simpleName, "Pret: $pretIndex")

        if (pretIndex !in 0..<5) {
            return
        }

        val ns = findNearestNote(pretIndex) ?: return
        val diff = ns.note.time - musicTime

        val callType = Call.typeWithTimeDiff(diff)
        Log.d(javaClass.simpleName, "Lane: $pretIndex, call=$callType diff=$diff")
    }


    private fun findNearestNote(lane: Int): NoteSprite? {
        var dist = Float.MAX_VALUE;
        var nearest:NoteSprite? = null;
        val notes = world.objectsAt(MainLayer.NOTE);
        val noteSpriteCount = notes.size
        for (i in 0..<noteSpriteCount) {
            val ns = notes[i] as NoteSprite ?: continue
            if (ns.note.pret != lane) continue
            var diff = ns.note.time - musicTime
            if (diff < 0) diff = -diff
            if (dist > diff) {
                dist = diff
                nearest = ns
            }
            return if (dist < 1.0f) nearest else null;
        }
        return null
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val handled = super.onTouchEvent(event)
        if (handled) return true

        if (event.action != MotionEvent.ACTION_DOWN &&
            event.action != MotionEvent.ACTION_MOVE
        ) {
            selectPret(-1)
        } else {
            val pt = gctx.metrics.fromScreen(event.x, event.y)
            val left = NoteSprite.LEFT - NoteSprite.X_SPACE / 2
            //if (pt.x < left) return false
            val lane = if (pt.x < left) -1 else ((pt.x - left) / NoteSprite.X_SPACE).toInt()
            //if (lane >= 5) return false
            selectPret(lane)
        }

        return true
    }

    override fun onPause() {
        super.onPause()
        // PauseScene 이 위에 올라오는 동안에도 ValueAnimator 는 Android framework 쪽에서 계속 진행될 수 있다.
        // 곡 종료 fade-out 은 MainScene 의 시간 흐름에 속하므로 Scene pause 와 함께 멈춰 둔다.
        finishFadeAnimator?.pause()
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
    }

    override fun onResume() {
        super.onResume()
        finishFadeAnimator?.resume()
        mediaPlayer?.start()
    }

    override fun onExit() {
        super.onExit()
        releaseMusic()
    }

    override fun onBackPressed(): Boolean {
        gctx.sceneStack.push(PauseScene(gctx))
        return true
    }
    private fun playMusic() {
        releaseMusic()

        // assets 안의 mp3 는 res/raw 처럼 resource id 로 열 수 없다.
        // AssetFileDescriptor 가 알려 주는 실제 fileDescriptor / startOffset / length 를
        // MediaPlayer 에 넘겨야 apk 안의 해당 파일 구간만 정확히 재생된다.
        val context = gctx.view.context
        val afd = runCatching {
            context.assets.openFd(song.mp3AssetPath)
        }.getOrElse {
            Log.w(javaClass.simpleName, "Cannot open music asset: ${song.mp3AssetPath}", it)
            return
        }

        mediaPlayer = MediaPlayer().apply {
            afd.use {
                setDataSource(it.fileDescriptor, it.startOffset, it.length)
            }
            setOnCompletionListener {
                releaseMusic()
            }
            prepare()
            start()
        }
    }

    private fun releaseMusic() {
        val animator = finishFadeAnimator
        finishFadeAnimator = null
        animator?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
