package com.sandy.seoul_matcheap

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.WorkManager
import com.naver.maps.map.NaverMapSdk
import com.sandy.seoul_matcheap.util.constants.APP_NAME
import com.sandy.seoul_matcheap.util.constants.NOTIFICATION_CHANNEL_ID
import com.sandy.seoul_matcheap.util.constants.NAVER_CLIENT_ID
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-17
 * @desc
 */

@HiltAndroidApp
class MatcheapApplication : Application() {

    @Inject lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        WorkManager.getInstance(this).cancelAllWork()

        setOnlyNightMode()

        initNaverMapSdkClient()
        createNotificationChannel()
    }

    private fun setOnlyNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initNaverMapSdkClient() {
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID)
    }

    private fun createNotificationChannel() {
        try {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                APP_NAME,
                IMPORTANCE_DEFAULT
            ).apply {
                enableLights(true)
                enableVibration(true)
                importance = IMPORTANCE_DEFAULT
            }
            notificationManager.createNotificationChannel(notificationChannel) // channel 생성
        } catch (e: Exception) { /* NO_OP */ }
    }

    companion object {
        fun showToastMessage(context: Context, message : String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
