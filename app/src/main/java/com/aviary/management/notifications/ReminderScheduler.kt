package com.aviary.management.notifications

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.aviary.management.worker.ReminderWorker
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    fun scheduleReminder(context: Context, title: String, body: String, delayMinutes: Long = 1) {
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(workDataOf(
                "title" to title,
                "body" to body
            ))
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
