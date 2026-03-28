package com.example.nudge.data.repository

import com.example.nudge.data.local.NudgeDao
import com.example.nudge.data.models.ChatMessage
import com.example.nudge.data.models.Goal
import com.example.nudge.data.models.Transaction
import com.example.nudge.data.remote.GroqApiClient
import com.example.nudge.data.remote.GroqMessage
import com.example.nudge.data.remote.GroqRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

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

        val currentGoal = getGoal().firstOrNull()
        val recentTransactions = getTransactions().firstOrNull() ?: emptyList()

        val response = getAiResponse(text, currentGoal, recentTransactions)
        val aiMsg = ChatMessage(text = response, isFromUser = false)
        nudgeDao.insertMessage(aiMsg)
    }

    private suspend fun getAiResponse(
        userInput: String,
        goal: Goal?,
        transactions: List<Transaction>
    ): String {
        return try {
            val goalInfo = goal?.let {
                "Meta: ${it.name}, Objetivo: $${it.targetAmount}, Ahorrado: $${it.currentSaved}."
            } ?: "El usuario aún no ha definido una meta de ahorro."

            val txInfo = if (transactions.isNotEmpty()) {
                "Últimos gastos: " + transactions.take(5).joinToString(", ") {
                    "${it.merchant} ($${it.amount} en ${it.category})"
                }
            } else {
                "No hay transacciones recientes."
            }

            val systemMsg = GroqMessage(
                role = "system",
                content = """
                    Eres Nudge, un mentor financiero pragmático y motivador.
                    Tu objetivo es mostrar el costo de oportunidad de los gastos triviales.

                    Contexto del usuario:
                    - $goalInfo
                    - $txInfo

                    Instrucciones:
                    1. Sé breve (máximo 2-3 oraciones).
                    2. Sé firme pero motivador.
                    3. Si el usuario pregunta por sus gastos, demuéstrale que los conoces.
                    4. Siempre relaciona el gasto con su meta de ahorro.
                """.trimIndent()
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
