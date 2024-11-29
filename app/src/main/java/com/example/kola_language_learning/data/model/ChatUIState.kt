package com.example.kola_language_learning.data.model

// data/model/ChatUIState.kt
sealed class ChatUIState {
    object Idle : ChatUIState()
    object Connecting : ChatUIState()
    object Recording : ChatUIState()
    object Processing : ChatUIState()
    object Playing : ChatUIState()
    data class Error(val message: String) : ChatUIState()
}