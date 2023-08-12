package com.sandy.seoul_matcheap.util.helper

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-24
 * @desc
 */
object PermissionHelper {

    fun isGrantedNotificationPermission(context: Context) = ActivityCompat.checkSelfPermission(context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PERMISSION_GRANTED

    fun isGrantedLocationPermission(context: Context) = ActivityCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    fun getPermissionSettingsIntent(context: Context) = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:" + context.packageName)
    }

}