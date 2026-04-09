package com.example.nudge.data.repository

import com.example.nudge.data.models.Transaction
import kotlinx.coroutines.flow.Flow

interface FinanceProvider {
    fun getRecentTransactions(): Flow<List<Transaction>>
    suspend fun simulateNewTransaction()
}
