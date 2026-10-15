package com.aviary.management.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aviary.management.notifications.NotificationHelper

class ReminderWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        // Simple reminder: read input data for title/body
        val title = inputData.getString("title") ?: "Feeding Reminder"
        val body = inputData.getString("body") ?: "It's time to feed your birds"
        NotificationHelper.createNotificationChannel(applicationContext)
        NotificationHelper.showNotification(applicationContext, (System.currentTimeMillis() % Int.MAX_VALUE).toInt(), title, body)
        return Result.success()
    }
}
