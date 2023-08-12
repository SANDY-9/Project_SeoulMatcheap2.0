package com.sandy.seoul_matcheap.ui.more.settings

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.repository.StoreRepository
import com.sandy.seoul_matcheap.ui.store.StoreDetailsActivity
import com.sandy.seoul_matcheap.util.*
import com.sandy.seoul_matcheap.util.constants.NOTIFICATION_CHANNEL_ID
import com.sandy.seoul_matcheap.util.constants.NOTIFICATION_ID
import com.sandy.seoul_matcheap.util.constants.STORE_ID
import com.sandy.seoul_matcheap.util.helper.DataHelper
import com.sandy.seoul_matcheap.util.helper.PermissionHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-05
 * @desc
 */

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val storeRepository: StoreRepository
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork() = run {
        try {
            val register = inputData.getBoolean(REGISTER, false)
            when {
                register -> registerPeriodWorker()
                else -> notifyNotification(createNotification())
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            NOTIFICATION_ID, createNotification()
        )
    }

    private fun registerPeriodWorker() {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                MIN_BACKOFF_MILLIS,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                PERIOD_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                periodicWorkRequest
            )
    }

    private val content: (String) -> String = { "우리 동네에 있는 착한가격업소 [$it] 어때요?" }
    private suspend fun createNotification() = withContext(Dispatchers.IO) {

        val store = storeRepository.downloadRecommendStore()
        val pendingIntent = PendingIntent.getActivity(context, 0,
            Intent(context, StoreDetailsActivity::class.java).apply {
                putExtra(STORE_ID, store.id)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return@withContext NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_title_notification)
            .setContentText(content(store.name))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_bookmark, ACTION_TITLE, pendingIntent)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun notifyNotification(notification: Notification) {
        if (PermissionHelper.isGrantedNotificationPermission(context)) {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID,
                notification
            )
        }
    }

    companion object {

        private const val ACTION_TITLE = "자세히 알아보기"
        private const val PERIOD_WORK_NAME = "matcheap_notification_period_work"
        private const val WORK_NAME = "matcheap_notification_register_work"
        private const val MIN_BACKOFF_MILLIS = 10L
        private const val REGISTER = "register"

        private fun registerNotificationSchedule(context: Context, time: Time) {
            val register = workDataOf(REGISTER to true)
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

}