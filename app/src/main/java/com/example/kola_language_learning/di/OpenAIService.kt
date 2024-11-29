package com.example.kola_language_learning.di

import androidx.multidex.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// data/api/OpenAIService.kt
class OpenAIService @Inject constructor() {
    private val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun connectWebSocket(listener: WebSocketListener): WebSocket {
        val request = Request.Builder()
            .url("wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-10-01")
//            .addHeader("Authorization", value = "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .addHeader("OpenAI-Beta", "realtime=v1")
            .build()

        return client.newWebSocket(request, listener)
    }
}