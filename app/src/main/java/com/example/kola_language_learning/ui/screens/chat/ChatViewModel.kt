package com.example.kola_language_learning.ui.screens.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kola_language_learning.data.audio.AudioRecorder
import com.example.kola_language_learning.data.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder
) : ViewModel() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _permissionNeeded = MutableStateFlow(false)
    val permissionNeeded = _permissionNeeded.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    fun toggleRecording() {
        if (!audioRecorder.hasRecordPermission()) {
            _permissionNeeded.value = true
            return
        }

        _isRecording.value = !_isRecording.value
        if (_isRecording.value) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    private fun startRecording() {
        viewModelScope.launch {
            audioRecorder.startRecording()
                .onFailure { error ->
                    // Handle error
                    Log.e("ChatViewModel", "Recording failed", error)
                }
        }
    }

    private fun stopRecording() {
        audioRecorder.stopRecording()
    }

    fun onPermissionGranted() {
        _permissionNeeded.value = false
        // Start recording if that's what user was trying to do
        if (_isRecording.value) {
            startRecording()
        }
    }
}
//// ChatViewModel.kt
//@HiltViewModel
//class ChatViewModel @Inject constructor() : ViewModel() {
//    private val _isRecording = MutableStateFlow(false)
//    val isRecording = _isRecording.asStateFlow()
//
//    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
//    val messages = _messages.asStateFlow()
//
//    fun toggleRecording() {
//        _isRecording.value = !_isRecording.value
//        if (_isRecording.value) {
//            startRecording()
//        } else {
//            stopRecording()
//        }
//    }
//
//    private fun startRecording() {
//        // Will implement audio recording here
//        viewModelScope.launch {
//            // Simulate recording for now
//            _messages.value = _messages.value + ChatMessage(
//                text = "Recording started...",
//                isUser = true
//            )
//        }
//    }
//
//    private fun stopRecording() {
//        // Will stop recording here
//        // For now, simulate AI response
//        viewModelScope.launch {
//            delay(1000)
//            _messages.value = _messages.value +  ChatMessage(
//                text = "Here's my response!",
//                isUser = false
//            )
//        }
//    }
//}