package com.example.nudge.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String? = null,
    val avatar_url: String? = null,
    val updated_at: String? = null
)
