package com.droids.playverse.sound
import android.content.Context
import android.media.SoundPool
import com.droids.playverse.R
import android.media.MediaPlayer
import com.droids.playverse.data.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private object AudioSettingsCache {

    @Volatile
    var soundEnabled: Boolean = true

    @Volatile
    var musicEnabled: Boolean = true

    private var started = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun start() {
        if (started) return
        started = true

        scope.launch {
            ServiceLocator.provideSettingsRepository().settings.collect { settings ->
                soundEnabled = settings.soundEnabled
                val musicWasEnabled = musicEnabled
                musicEnabled = settings.musicEnabled

                if (musicWasEnabled && !musicEnabled) {
                    MusicManager.pause()
                } else if (!musicWasEnabled && musicEnabled) {
                    MusicManager.resumeIfWasPlaying()
                }
            }
        }
    }
}

object SoundManager {

    private var soundPool: SoundPool? = null
    private var isLoaded = false
    private var popSound = 0
    private var bottleSound = 0
    private var starSound =0
    private var hitSound =0
    private var brickHitSound =0
    private var brickPointSound =0
    private var catchSound =0
    private var bombSound =0
    private var winSound =0
    private var drawSound =0
    private var o_Sound =0
    private var x_sound =0
    private var noPointSound =0
    private var pointSound =0
    fun init(context: Context, isPreview: Boolean) {
        if (isPreview || soundPool != null) return

        AudioSettingsCache.start()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()

        soundPool?.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
            }
        }

        popSound = soundPool!!.load(context, R.raw.pop_sound, 1)
        bottleSound = soundPool!!.load(context, R.raw.bottle_open_sound, 1)
        starSound=soundPool!!.load(context,R.raw.star,1)
        hitSound=soundPool!!.load(context,R.raw.block_hit,1)
        brickHitSound=soundPool!!.load(context,R.raw.ball_hit,1)
        brickPointSound=soundPool!!.load(context,R.raw.brick_point,1)
        catchSound=soundPool!!.load(context,R.raw.catch_sound,1)
        bombSound=soundPool!!.load(context,R.raw.bomb_sound,1)
        winSound=soundPool!!.load(context,R.raw.win_sound,1)
        drawSound=soundPool!!.load(context,R.raw.draw_sound,1)
        o_Sound=soundPool!!.load(context,R.raw.o_sound,1)
        x_sound=soundPool!!.load(context,R.raw.x_sound,1)
        noPointSound=soundPool!!.load(context,R.raw.no_point,1)
        pointSound=soundPool!!.load(context,R.raw.point,1)



    }

    fun playPop() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            popSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun playBottleSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            bottleSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun playStarSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            starSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }

    fun playHitSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            hitSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun playBrickHitSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            brickHitSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }

    fun playBrickPointSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            brickPointSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun playCatchSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            catchSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun playBombSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            bombSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun playWinSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            winSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun playDrawSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            drawSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun play_oSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            o_Sound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun play_xSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            x_sound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun playPointSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            pointSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
    fun playNoPointSound() {
        if (!isLoaded || !AudioSettingsCache.soundEnabled) return

        soundPool?.play(
            noPointSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }





    fun release() {
        soundPool?.release()
        soundPool = null
        isLoaded = false
    }
}

object MusicManager {

    private var mediaPlayer: MediaPlayer? = null
    private var lastContext: Context? = null
    private var wasPlaying = false

    fun start(context: Context) {
        lastContext = context.applicationContext
        AudioSettingsCache.start()
        wasPlaying = true

        if (!AudioSettingsCache.musicEnabled) return

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.game_bg)
            mediaPlayer?.isLooping = true // 🔁 LOOP FOREVER
        }
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        wasPlaying = false
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    internal fun resumeIfWasPlaying() {
        if (!wasPlaying) return
        val context = lastContext ?: return

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.game_bg)
            mediaPlayer?.isLooping = true
        }
        mediaPlayer?.start()
    }
}