package com.example.kola_language_learning.ui.screens.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kola_language_learning.data.audio.AudioPlayer
import com.example.kola_language_learning.data.audio.AudioRecorder
import com.example.kola_language_learning.data.model.ChatMessage
import com.example.kola_language_learning.data.model.ChatUIState
import com.example.kola_language_learning.openai.OpenAIRequest
import com.example.kola_language_learning.openai.ResponseConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.UUID
import javax.inject.Inject


// Add data class for WebSocket responses
@Serializable
data class WebSocketResponse(
    val type: String,
    val content: String,
    val eventId: String? = null
)

// ChatViewModel.kt
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer
) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatUIState>(ChatUIState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _permissionNeeded = MutableStateFlow(false)
    val permissionNeeded = _permissionNeeded.asStateFlow()

    // Add this function
    fun onPermissionGranted() {
        _permissionNeeded.value = false
        if (_isRecording.value) {
            startRecording()
        }
    }

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            viewModelScope.launch {
                _uiState.value = ChatUIState.Recording
                // Send initial configuration
                val config = OpenAIRequest(
                    eventId = UUID.randomUUID().toString(),
                    response = ResponseConfig(
                        instructions = "You are a helpful language learning assistant. Help the user practice speaking.",
                    )
                )
                webSocket.send(Json.encodeToString(config))
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            viewModelScope.launch {
                try {
                    val response = Json.decodeFromString<WebSocketResponse>(text)
                    when (response.type) {
                        "transcript" -> {
                            _messages.value = _messages.value + ChatMessage(
                                text = response.content,
                                isUser = true
                            )
                        }
                        "response" -> {
                            _messages.value = _messages.value + ChatMessage(
                                text = response.content,
                                isUser = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = ChatUIState.Error("Failed to process message")
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            viewModelScope.launch {
                try {
                    audioPlayer.playAudio(bytes.toByteArray())
                    _uiState.value = ChatUIState.Playing
                } catch (e: Exception) {
                    _uiState.value = ChatUIState.Error("Failed to play audio")
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            viewModelScope.launch {
                _uiState.value = ChatUIState.Error("Connection failed: ${t.message}")
                stopRecording()
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            viewModelScope.launch {
                if (_uiState.value !is ChatUIState.Error) {
                    _uiState.value = ChatUIState.Idle
                }
            }
        }
    }

    fun toggleRecording() {
        if (!_isRecording.value) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    private fun startRecording() {
        viewModelScope.launch {
            try {
                _uiState.value = ChatUIState.Connecting
                audioRecorder.startRecording(viewModelScope, webSocketListener)
                    .onSuccess {
                        _isRecording.value = true
                    }
                    .onFailure { error ->
                        _uiState.value = ChatUIState.Error("Failed to start recording: ${error.message}")
                    }
            } catch (e: Exception) {
                _uiState.value = ChatUIState.Error("Recording error: ${e.message}")
            }
        }
    }

    private fun stopRecording() {
        _isRecording.value = false
        audioRecorder.stopRecording()
        if (_uiState.value !is ChatUIState.Error) {
            _uiState.value = ChatUIState.Idle
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopRecording()
        audioPlayer.stopPlaying()
    }
}





//@HiltViewModel
//class ChatViewModel @Inject constructor(
//    private val audioRecorder: AudioRecorder,
//    private val audioPlayer: AudioPlayer
//) : ViewModel() {
//    // i added this
//    private val _uiState = MutableStateFlow<ChatUIState>(ChatUIState.Idle)
//    val uiState = _uiState.asStateFlow()
//    // just to see what i added
//
//    private val _isRecording = MutableStateFlow(false)
//    val isRecording = _isRecording.asStateFlow()
//
//    private val _permissionNeeded = MutableStateFlow(false)
//    val permissionNeeded = _permissionNeeded.asStateFlow()
//
//    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
//    val messages = _messages.asStateFlow()
//
//    // Add states for websocket connection
//    private val _isConnected = MutableStateFlow(false)
//    val isConnected = _isConnected.asStateFlow()
//
//    private val _audioResponse = MutableStateFlow<ByteArray?>(null)
//    val audioResponse = _audioResponse.asStateFlow()
//
//    private val webSocketListener = object : WebSocketListener() {
//        override fun onMessage(webSocket: WebSocket, text: String) {
//            // Handle text responses (transcripts and AI messages)
//            viewModelScope.launch {
//                val message = Json.decodeFromString<WebSocketResponse>(text)
//                when (message.type) {
//                    "transcript" -> {
//                        _messages.value = _messages.value + ChatMessage(
//                            text = message.content,
//                            isUser = true
//                        )
//                    }
//                    "response" -> {
//                        _messages.value = _messages.value + ChatMessage(
//                            text = message.content,
//                            isUser = false
//                        )
//                    }
//                }
//            }
//        }
//
//        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//            // Handle audio responses
//            viewModelScope.launch {
//                _audioResponse.value = bytes.toByteArray()
//            }
//        }
//
//        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//            _isConnected.value = false
//        }
//    }
//
//    fun toggleRecording() {
//        if (!audioRecorder.hasRecordPermission()) {
//            _permissionNeeded.value = true
//            return
//        }
//
//        _isRecording.value = !_isRecording.value
//        if (_isRecording.value) {
//            startRecording()
//        } else {
//            stopRecording()
//        }
//    }
//
//    private fun startRecording() {
//        viewModelScope.launch {
//            try {
//                audioRecorder.startRecording(viewModelScope)
//                    .onSuccess {
//                        // Recording started successfully
//                    }
//                    .onFailure { error ->
//                        // Handle recording error
//                        Log.e("ChatViewModel", "Recording failed", error)
//                        _isRecording.value = false
//                    }
//            } catch (e: Exception) {
//                Log.e("ChatViewModel", "Error starting recording", e)
//                _isRecording.value = false
//            }
//        }
//    }
//
//    private fun stopRecording() {
//        audioRecorder.stopRecording()
//    }
//
//    fun onPermissionGranted() {
//        _permissionNeeded.value = false
//        if (_isRecording.value) {
//            startRecording()
//        }
//    }
//
////    override fun onCleared() {
////        super.onCleared()
////        stopRecording()
////    }
//    val isPlayingResponse = audioPlayer.isPlaying
//
//        init {
//            // Listen for audio responses and play them
//            viewModelScope.launch {
//                audioResponse.collect { audioData ->
//                    audioData?.let {
//                        audioPlayer.playAudio(it)
//                    }
//                }
//            }
//        }
//
//    override fun onCleared() {
//        super.onCleared()
//        stopRecording()
//        audioPlayer.stopPlaying()
//    }
//
//    private fun updateUIState() {
//        val newState = when {
//            !_isConnected.value -> ChatUIState.Connecting
//            _isRecording.value -> ChatUIState.Recording
//            audioPlayer.isPlaying.value -> ChatUIState.Playing
//            else -> ChatUIState.Idle
//        }
//        _uiState.value = newState
//    }
//
//}
//
//// Add data class for WebSocket responses
//@Serializable
//data class WebSocketResponse(
//    val type: String,
//    val content: String,
//    val eventId: String? = null
//)


