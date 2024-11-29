package com.example.kola_language_learning.data.audio

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.kola_language_learning.di.OpenAIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.nio.ByteBuffer
import java.nio.charset.Charset
import javax.inject.Inject


class AudioRecorder @Inject constructor(
    private val context: Context,
    private val openAIService: OpenAIService
) {
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var webSocket: WebSocket? = null
    private var listener: WebSocketListener? = null

    fun hasRecordPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }


    @SuppressLint("MissingPermission")
    fun startRecording(
        coroutineScope: CoroutineScope,
        webSocketListener: WebSocketListener
    ): Result<Unit> {
        if (!hasRecordPermission()) {
            return Result.failure(SecurityException("Recording permission not granted"))
        }

        return try {
            val bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT
            )

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            audioRecord?.startRecording()

            // Connect WebSocket with provided listener
            listener = webSocketListener
            webSocket = openAIService.connectWebSocket(webSocketListener)

            // Start recording in coroutine
            recordingJob = coroutineScope.launch(Dispatchers.IO) {
                val buffer = ShortArray(bufferSize)
                while (isActive) {
                    val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                    if (read > 0) {
                        // Convert ShortArray to ByteArray
                        val byteBuffer = ByteBuffer.allocate(read * 2) // 2 bytes per short
                        for (i in 0 until read) {
                            byteBuffer.putShort(buffer[i])
                        }
                        val byteArray = byteBuffer.array()

                        // Send audio data through WebSocket
                        webSocket?.send(byteBuffer.toByteString())
                    }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun stopRecording() {
        recordingJob?.cancel()
        recordingJob = null
        webSocket?.close(1000, "Recording finished")
        webSocket = null
        listener = null
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}

//class AudioRecorder @Inject constructor(
//    private val context: Context
//) {
//    private var audioRecord: AudioRecord? = null
//    private var isRecording = false
//
//    companion object {
//        private const val SAMPLE_RATE = 16000
//        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
//        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
//    }
//
//    fun hasRecordPermission(): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.RECORD_AUDIO
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    @SuppressLint("MissingPermission")
//    fun startRecording(viewModelScope: CoroutineScope): Result<Unit> {
//        if (!hasRecordPermission()) {
//            return Result.failure(SecurityException("Recording permission not granted"))
//        }
//
//        return try {
//            val bufferSize = AudioRecord.getMinBufferSize(
//                SAMPLE_RATE,
//                CHANNEL_CONFIG,
//                AUDIO_FORMAT
//            )
//
//            // We can suppress the lint warning since we checked permission above
//            audioRecord = AudioRecord(
//                MediaRecorder.AudioSource.MIC,
//                SAMPLE_RATE,
//                CHANNEL_CONFIG,
//                AUDIO_FORMAT,
//                bufferSize
//            )
//
//            audioRecord?.startRecording()
//            isRecording = true
//            readAudioData(bufferSize)
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    private fun readAudioData(bufferSize: Int) {
//        val buffer = ShortArray(bufferSize)
//
//        while (isRecording) {
//            val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
//            if (read > 0) {
//                Log.d("AudioRecorder", "Read $read bytes of audio")
//            }
//        }
//    }
//
//    fun stopRecording() {
//        isRecording = false
//        audioRecord?.stop()
//        audioRecord?.release()
//        audioRecord = null
//    }
//}