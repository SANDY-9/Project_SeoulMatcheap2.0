package com.sandy.seoul_matcheap.ui.more.settings.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.repository.StoreRepository
import com.sandy.seoul_matcheap.ui.store.StoreDetailsActivity
import com.sandy.seoul_matcheap.util.constants.ACTION_TITLE
import com.sandy.seoul_matcheap.util.constants.DEFAULT_HOUR
import com.sandy.seoul_matcheap.util.constants.DEFAULT_MINUTE
import com.sandy.seoul_matcheap.util.constants.HOUR
import com.sandy.seoul_matcheap.util.constants.MINUTE
import com.sandy.seoul_matcheap.util.constants.NOTIFICATION_CHANNEL_ID
import com.sandy.seoul_matcheap.util.constants.NOTIFICATION_ID
import com.sandy.seoul_matcheap.util.constants.STORE_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-08-25
 * @desc
 */

@AndroidEntryPoint
class NotificationReceiver: BroadcastReceiver() {

    @Inject
    lateinit var storeRepository: StoreRepository
    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        val hour = intent?.getIntExtra(HOUR, DEFAULT_HOUR) ?: return
        val min = intent?.getIntExtra(MINUTE, DEFAULT_MINUTE) ?: return

        if(context != null) {
            CoroutineScope(Dispatchers.IO).launch {

                val store = storeRepository.downloadRecommendStore()
                val pendingIntent = PendingIntent.getActivity(
                    context, 0,
                    Intent(context, StoreDetailsActivity::class.java).apply {
                        putExtra(STORE_ID, store.id)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )

                createNotification(context, store.name, pendingIntent)
                notificationScheduler.schedule(hour to min)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun createNotification(context: Context, storeName:String, pendingIntent: PendingIntent) {
        val content = "우리 동네에 있는 착한가격업소 [$storeName] 어때요?"
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_title_notification)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_bookmark, ACTION_TITLE, pendingIntent)
            .build()
        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID,
            notification
        )
    }


}