package com.sandy.seoul_matcheap.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sandy.seoul_matcheap.ui.more.settings.Time
import com.sandy.seoul_matcheap.util.constants.HOUR
import com.sandy.seoul_matcheap.util.constants.MINUTE
import com.sandy.seoul_matcheap.util.constants.NOTIFICATION_REQUEST_CODE
import java.util.Calendar
import javax.inject.Inject

class NotificationSchedulerImpl @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager
) : NotificationScheduler {

    override fun schedule(time: Time) {
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.first)
            set(Calendar.MINUTE, time.second)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if(before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(HOUR, time.first)
            putExtra(MINUTE, time.second)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dueTime.timeInMillis,
            PendingIntent.getBroadcast(
                context,
                NOTIFICATION_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel() {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                NOTIFICATION_REQUEST_CODE,
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun setNotificationSchedule(register: Boolean, time: Time) {
        when {
            register -> schedule(time)
            else -> cancel()
        }
    }
}