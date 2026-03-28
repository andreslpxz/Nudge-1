package com.example.nudge.data.repository

import com.example.nudge.data.models.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.random.Random

class MockFinanceProvider : FinanceProvider {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())

    init {
        // Initial mock data
        _transactions.value = listOf(
            Transaction(UUID.randomUUID().toString(), "Starbucks", 5.50, "Food & Drink"),
            Transaction(UUID.randomUUID().toString(), "Netflix", 15.99, "Entertainment"),
            Transaction(UUID.randomUUID().toString(), "Uber", 12.00, "Transport")
        )
    }

    override fun getRecentTransactions() = _transactions.asStateFlow()

    override suspend fun simulateNewTransaction() {
        val merchants = listOf("Amazon", "Spotify", "McDonalds", "Zara", "Gas Station")
        val categories = listOf("Shopping", "Entertainment", "Food & Drink", "Shopping", "Transport")
        val index = Random.nextInt(merchants.size)

        val newTx = Transaction(
            id = UUID.randomUUID().toString(),
            merchant = merchants[index],
            amount = Random.nextDouble(5.0, 50.0),
            category = categories[index]
        )
        _transactions.value = _transactions.value + newTx
    }
}
