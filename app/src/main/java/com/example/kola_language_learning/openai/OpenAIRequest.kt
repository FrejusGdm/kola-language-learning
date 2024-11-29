package com.example.kola_language_learning.openai

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class OpenAIRequest(
    val eventId: String,
    val type: String = "response.create",
    val response: ResponseConfig
)

@Serializable
data class ResponseConfig(
    val modalities: List<String> = listOf("text", "audio"),
    val instructions: String,
    val voice: String = "alloy",
    val outputAudioFormat: String = "pcm16",
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 150
)