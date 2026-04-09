package com.example.nudge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nudge.data.models.ChatMessage
import com.example.nudge.data.models.Goal
import com.example.nudge.data.models.Transaction
import com.example.nudge.data.repository.NudgeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NudgeViewModel(private val repository: NudgeRepository) : ViewModel() {
    val goal: Flow<Goal?> = repository.getGoal()
    val messages: Flow<List<ChatMessage>> = repository.getChatMessages()
    val transactions: Flow<List<Transaction>> = repository.getTransactions()

    fun saveGoal(goal: Goal) {
        viewModelScope.launch {
            repository.saveGoal(goal)
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            repository.sendMessage(text)
        }
    }

    fun simulateTransaction() {
        viewModelScope.launch {
            repository.simulateTransaction()
        }
    }
}
