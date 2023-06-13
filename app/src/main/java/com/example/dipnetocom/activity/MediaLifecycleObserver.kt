package com.example.dipnetocom.activity

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class MediaLifecycleObserver : LifecycleEventObserver {
    var player: MediaPlayer? = MediaPlayer()

    private val currentTrack = MutableLiveData<String?>(null)

    fun playPause(url: String): Boolean {

        return if (currentTrack.value != url) {
            reset()
            play(url)
            currentTrack.value = url
            true
        } else {
            if (player?.isPlaying == true) {
                pause()
                false
            } else {
                resume()
                true
            }
        }
    }

    private fun play(url: String) {
        player?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        player?.setDataSource(url)
        player?.prepare()
        player?.setOnPreparedListener {
            it.start()
        }
    }

    private fun pause() {
        player?.pause()
    }

    private fun resume() {
        player?.start()
    }

    private fun reset() {
        player?.reset()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> player?.pause()
            Lifecycle.Event.ON_STOP -> {
                player?.release()
                player = null
            }

            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> Unit
        }
    }
}