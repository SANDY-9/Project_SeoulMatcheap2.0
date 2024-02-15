package com.sandy.seoul_matcheap.notification

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest.Companion.MIN_BACKOFF_MILLIS
import androidx.work.workDataOf
import com.sandy.seoul_matcheap.notification.NotificationWorker.Companion.REGISTER
import com.sandy.seoul_matcheap.ui.more.settings.Time
import com.sandy.seoul_matcheap.util.helper.DataHelper
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val WORK_NAME = "matcheap_notification_register_work"

    private fun registerNotificationSchedule(context: Context, time: Time) {
        val register = workDataOf(REGISTER to true)
        val n = DataHelper.calculateDuration(time.first, time.second)
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(register)
            .setInitialDelay(
                DataHelper.calculateDuration(time.first, time.second),
                TimeUnit.SECONDS
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                MIN_BACKOFF_MILLIS,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelNotificationSchedule(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }


    fun setNotificationSchedule(context: Context, isActive: Boolean, time: Time) {
        when {
            isActive -> registerNotificationSchedule(context, time)
            else -> cancelNotificationSchedule(context)
        }
    }

}