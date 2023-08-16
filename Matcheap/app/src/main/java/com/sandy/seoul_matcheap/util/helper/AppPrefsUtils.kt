package com.sandy.seoul_matcheap.util.helper

import android.content.SharedPreferences
import com.sandy.seoul_matcheap.util.constants.*

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-24
 * @desc
 */
object AppPrefsUtils {

    fun saveLatestDatabaseVersion(prefs: SharedPreferences, version: String)  = prefs.edit()
        .putString(APP_DATABASE_STATE, version)
        .apply()
    fun getSavedDatabaseVersion(prefs: SharedPreferences) = prefs.getString(APP_DATABASE_STATE, null)

    fun getSavedAppVersion(prefs: SharedPreferences) = prefs.getString(APP_VERSION, null)
    fun saveLatestAppVersion(prefs: SharedPreferences, version: String) = prefs.edit()
        .putString(APP_VERSION, version)
        .apply()

    fun saveTimeSettings(prefs: SharedPreferences, hour: Int, min: Int) = prefs.edit()
        .putInt(HOUR, hour)
        .putInt(MINUTE, min)
        .apply()
    fun getSavedTime(prefs: SharedPreferences) = prefs.run {
        getInt(HOUR, DEFAULT_HOUR) to getInt(MINUTE, DEFAULT_MINUTE)
    }

    fun getNotificationPermissionState(prefs: SharedPreferences) = prefs.getBoolean(
        APP_NOTIFICATION_PERMISSION_STATE, false)

    fun setNotificationPermissionState(prefs: SharedPreferences, state: Boolean) = prefs.edit()
        .putBoolean(APP_NOTIFICATION_PERMISSION_STATE, state)
        .apply()

    fun getNotificationState(prefs: SharedPreferences) = prefs.getBoolean(
        APP_NOTIFICATION_STATE, false)

    fun setNotificationState(prefs: SharedPreferences, state: Boolean) = prefs.edit()
        .putBoolean(APP_NOTIFICATION_STATE, state)
        .apply()

}