package com.example.kola_language_learning.data.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

// data/audio/AudioPlayer.kt
class AudioPlayer @Inject constructor(
    private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    fun playAudio(audioData: ByteArray) {
        stopPlaying() // Stop any current playback

        try {
            // Create a temporary file to store the audio data
            val tempFile = File.createTempFile("audio", null, context.cacheDir).apply {
                deleteOnExit()
                writeBytes(audioData)
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.path)
                setOnCompletionListener {
                    _isPlaying.value = false
                    stopPlaying()
                }
                setOnPreparedListener {
                    _isPlaying.value = true
                    start()
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error playing audio", e)
            _isPlaying.value = false
        }
    }

    fun stopPlaying() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        _isPlaying.value = false
    }
}