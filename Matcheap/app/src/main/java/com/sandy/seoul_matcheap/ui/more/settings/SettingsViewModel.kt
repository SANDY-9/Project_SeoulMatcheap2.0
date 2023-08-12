package com.sandy.seoul_matcheap.ui.more.settings

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.sandy.seoul_matcheap.util.*
import com.sandy.seoul_matcheap.util.constants.APP_PREFS_SETTINGS
import com.sandy.seoul_matcheap.util.helper.AppPrefsUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-23
 * @desc
 */

typealias Time = Pair<Int, Int>

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @Named(APP_PREFS_SETTINGS) private val prefs: SharedPreferences,
    application: Application
) : AndroidViewModel(application) {

    val isGranted = MutableLiveData<Boolean>()

    val hour = MutableLiveData<Int>()
    val minute = MutableLiveData<Int>()

    private val _savedTime = MutableLiveData<Time>()
    val savedTime: LiveData<Time> = _savedTime

    init {
        isGranted.value = AppPrefsUtils.getNotificationState(prefs)

        val savedTime = AppPrefsUtils.getSavedTime(prefs)
        hour.value = savedTime.first
        minute.value = savedTime.second
        _savedTime.value = savedTime
    }

    fun initTime() {
        val time = getCurSavedTime()
        hour.value = time.first
        minute.value = time.second
    }

    private fun getCurSavedTime() = savedTime.value!!
    private fun setSavedTime(hour: Int, min: Int) {
        _savedTime.value = hour to min
    }

    fun saveTime() {
        val hour = hour.value!!
        val min = minute.value!!
        AppPrefsUtils.saveTimeSettings(
            prefs = prefs,
            hour = hour,
            min = min
        )
        setSavedTime(hour, min)
        updateWorkerSchedule(true)
    }

    fun setNotificationState(state: Boolean) {
        AppPrefsUtils.setNotificationState(prefs, state)
        initTime()
        updateWorkerSchedule(state)
    }

    private fun updateWorkerSchedule(isActive: Boolean) {
        val context = getApplication<Application>().applicationContext
        NotificationWorker.setNotificationSchedule(context, isActive, getCurSavedTime())
    }

}