package com.example.nudge.data.remote

import com.google.gson.annotations.SerializedName

data class GroqRequest(
    val messages: List<GroqMessage>,
    val model: String = "llama-3.3-70b-versatile"
)

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqResponse(
    val choices: List<GroqChoice>
)

data class GroqChoice(
    val message: GroqMessage
)

interface GroqApiService {
    @retrofit2.http.POST("v1/chat/completions")
    suspend fun getCompletion(
        @retrofit2.http.Header("Authorization") authHeader: String,
        @retrofit2.http.Body request: GroqRequest
    ): GroqResponse
}
