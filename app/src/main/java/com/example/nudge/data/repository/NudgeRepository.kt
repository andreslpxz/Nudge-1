package com.example.nudge.data.repository

import com.example.nudge.data.local.NudgeDao
import com.example.nudge.data.models.ChatMessage
import com.example.nudge.data.models.Goal
import com.example.nudge.data.remote.GroqApiClient
import com.example.nudge.data.remote.GroqMessage
import com.example.nudge.data.remote.GroqRequest
import kotlinx.coroutines.flow.Flow

class NudgeRepository(
    private val nudgeDao: NudgeDao,
    private val financeProvider: FinanceProvider
) {
    fun getGoal(): Flow<Goal?> = nudgeDao.getGoal()

    suspend fun saveGoal(goal: Goal) = nudgeDao.insertGoal(goal)

    fun getChatMessages(): Flow<List<ChatMessage>> = nudgeDao.getAllMessages()

    suspend fun sendMessage(text: String) {
        val userMsg = ChatMessage(text = text, isFromUser = true)
        nudgeDao.insertMessage(userMsg)

        val response = getAiResponse(text)
        val aiMsg = ChatMessage(text = response, isFromUser = false)
        nudgeDao.insertMessage(aiMsg)
    }

    private suspend fun getAiResponse(userInput: String): String {
        return try {
            val systemMsg = GroqMessage(
                role = "system",
                content = "Eres Nudge, un mentor financiero pragmático y motivador. Tu objetivo no es prohibir gastos, sino mostrar el costo de oportunidad. Si el usuario gasta en algo trivial, recuérdale su meta. Sé breve, directo y ofrece siempre una alternativa de ahorro inmediata."
            )
            val userMsg = GroqMessage(role = "user", content = userInput)
            val response = GroqApiClient.service.getCompletion(
                GroqApiClient.getAuthHeader(),
                GroqRequest(messages = listOf(systemMsg, userMsg))
            )
            response.choices.firstOrNull()?.message?.content ?: "Lo siento, no pude procesar eso."
        } catch (e: Exception) {
            "Error de conexión con Nudge: ${e.localizedMessage}"
        }
    }

    fun getTransactions() = financeProvider.getRecentTransactions()

    suspend fun simulateTransaction() = financeProvider.simulateNewTransaction()
}
