package kr.ac.tukorea.ge.spgp2026.taptu.game.scene.main

import android.media.MediaPlayer
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.data.SongCatalog
import kr.ac.tukorea.ge.spgp2026.taptu.game.layer.MainLayer
import kr.ac.tukorea.ge.spgp2026.taptu.game.objs.NoteGenerator
import kr.ac.tukorea.ge.spgp2026.taptu.res.BitmapBlur

class MainScene(
    gctx: GameContext,
    songIndex: Int,
) : Scene(gctx) {
    override val world = World<MainLayer>(MainLayer.entries.toTypedArray())
    val song = SongCatalog.songs[songIndex]
    private var mediaPlayer: MediaPlayer? = null
    val musicTime: Float
        get() = (mediaPlayer?.currentPosition ?: 0) / 1000f

    override fun onEnter() {
        val screenWidth = gctx.metrics.width
        val screenHeight = gctx.metrics.height
        val centerX = screenWidth / 2
        val centerY = screenHeight / 2

        val context = gctx.view.context
        song.loadNotes(context.assets)

        val thumbnail = song.loadThumbnail(context.assets) ?: gctx.res.getBitmap(R.mipmap.default_thumbnail)
        val blurredThumbnail = BitmapBlur.blurBitmap(context, thumbnail)
        val albumCover = Sprite(gctx, bitmap = blurredThumbnail, resId = R.mipmap.default_thumbnail)
        albumCover.setCenterProportionalHeight(
            centerX,
            centerY,
            screenHeight,
        )
        world.add(albumCover, MainLayer.BG)

        val bg = Sprite(gctx, R.mipmap.bg)
        bg.setCenterProportionalWidth(centerX, centerY, screenWidth)
        world.add(bg, MainLayer.BG)

        world.add(NoteGenerator(song, world) { musicTime }, MainLayer.GENERATOR)

        playMusic()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
    }

    override fun onExit() {
        super.onExit()
        releaseMusic()
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
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
