package com.example.nudge

import android.app.Application
import androidx.work.*
import com.example.nudge.worker.NudgeWorker
import java.util.concurrent.TimeUnit

class NudgeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleNudgeWorker()
    }

    private fun scheduleNudgeWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<NudgeWorker>(4, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "nudge_periodic_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}
