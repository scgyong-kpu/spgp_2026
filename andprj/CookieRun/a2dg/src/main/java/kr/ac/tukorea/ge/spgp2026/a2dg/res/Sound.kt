package kr.ac.tukorea.ge.spgp2026.a2dg.res

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

// Sound 는 게임에서 쓰는 짧은 효과음과 반복 배경음을 다루는 helper 이다.
//
// 짧은 효과음은 SoundPool 을 사용한다.
// SoundPool 은 jump, item, hurt 처럼 매우 짧고 자주 재생되는 소리에 적합하다.
// 처음 재생할 때 raw resource 를 SoundPool 에 load 해 두고, 이후에는 soundId 를 재사용한다.
//
// 배경음은 MediaPlayer 를 사용한다.
// MediaPlayer 는 긴 음악 파일을 반복 재생하거나 pause/resume 하는 데 적합하다.
class Sound(
    context: Context,
) {
    // GameResources 가 Sound 인스턴스를 소유하고, GameContext 는 GameResources 를 소유한다.
    // 따라서 소리를 내려는 객체는 gctx 를 기억하고 있다가 gctx.res.sound.playEffect(...) 처럼 접근한다.
    //
    // Activity context 를 오래 들고 있으면 Activity 가 끝난 뒤에도 메모리에 남을 수 있다.
    // 그래서 applicationContext 를 저장해 앱 전체 생명주기에 맞춰 사용한다.
    private val appContext = context.applicationContext
    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<Int, Int>()

    fun playMusic(resId: Int) {
        stopMusic()
        mediaPlayer = MediaPlayer.create(appContext, resId).apply {
            isLooping = true
            start()
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun resumeMusic() {
        mediaPlayer?.start()
    }

    fun playEffect(resId: Int) {
        val pool = getSoundPool()
        val soundId = soundIds[resId] ?: pool.load(appContext, resId, PRIORITY).also {
            soundIds[resId] = it
        }
        pool.play(soundId, VOLUME, VOLUME, PRIORITY, NO_LOOP, NORMAL_RATE)
    }

    fun release() {
        stopMusic()
        soundPool?.release()
        soundPool = null
        soundIds.clear()
    }

    private fun getSoundPool(): SoundPool {
        soundPool?.let { return it }

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        return SoundPool.Builder()
            .setAudioAttributes(attrs)
            .setMaxStreams(MAX_STREAMS)
            .build()
            .also { soundPool = it }
    }

    companion object {
        private const val MAX_STREAMS = 3
        private const val PRIORITY = 1
        private const val VOLUME = 1f
        private const val NO_LOOP = 0
        private const val NORMAL_RATE = 1f
    }
}
