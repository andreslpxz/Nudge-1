package com.example.nudge.data.remote

import com.example.nudge.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GroqApiClient {
    private const val BASE_URL = "https://api.groq.com/openai/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: GroqApiService = retrofit.create(GroqApiService::class.java)

    fun getAuthHeader(): String = "Bearer ${BuildConfig.GROQ_API_KEY}"
}
