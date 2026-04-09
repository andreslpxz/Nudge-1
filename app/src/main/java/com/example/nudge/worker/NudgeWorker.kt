package com.example.nudge.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nudge.data.local.AppDatabase
import com.example.nudge.data.models.ChatMessage
import com.example.nudge.data.remote.GroqApiClient
import com.example.nudge.data.remote.GroqMessage
import com.example.nudge.data.remote.GroqRequest
import kotlinx.coroutines.flow.first

class NudgeWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val CHANNEL_ID = "nudge_notifications"

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.nudgeDao()
        val goal = dao.getGoal().first() ?: return Result.success()

        val systemPrompt = "Eres Nudge, un mentor financiero pragmático y motivador. Tu objetivo no es prohibir gastos, sino mostrar el costo de oportunidad. Si el usuario gasta en algo trivial, recuérdale su meta (${goal.name}). Sé breve, directo y ofrece siempre una alternativa de ahorro inmediata."
        val analysisPrompt = "Detecté un patrón de gasto innecesario en suscripciones digitales esta semana. ¿Qué me dirías para ayudarme a ahorrar para mi meta de ${goal.name}?"

        return try {
            val response = GroqApiClient.service.getCompletion(
                GroqApiClient.getAuthHeader(),
                GroqRequest(messages = listOf(
                    GroqMessage(role = "system", content = systemPrompt),
                    GroqMessage(role = "user", content = analysisPrompt)
                ))
            )
            val nudgeText = response.choices.firstOrNull()?.message?.content
                ?: "Si compras ese café hoy, estarás un poco más lejos de tu viaje a Japón. ¡Mejor ahorra esos $5!"

            // Insert the proactive nudge as a chat message
            dao.insertMessage(ChatMessage(text = nudgeText, isFromUser = false))

            // Show a system notification
            showNotification(nudgeText)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Nudge Alerts", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nudge: Mentor Financiero")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
