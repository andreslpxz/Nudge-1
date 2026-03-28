package com.example.nudge.data.models

data class Transaction(
    val id: String,
    val merchant: String,
    val amount: Double,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)
