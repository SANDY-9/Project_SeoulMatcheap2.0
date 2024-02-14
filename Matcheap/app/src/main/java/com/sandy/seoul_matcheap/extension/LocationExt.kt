package com.sandy.seoul_matcheap.extension

import android.content.Context
import android.location.LocationManager
import com.sandy.seoul_matcheap.MatcheapApplication.Companion.showToastMessage
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.util.constants.MESSAGE_GPS_COMPLETE_DESC
import com.sandy.seoul_matcheap.util.constants.MESSAGE_GPS_DESC
import com.sandy.seoul_matcheap.util.constants.MESSAGE_GPS_WARNING

fun LocationManager.updateLocation(locationViewModel: LocationViewModel, context: Context) : Boolean {
    showToastMessage(context, MESSAGE_GPS_DESC)
    val isLocationUpdateEnabled = isProviderEnabled(LocationManager.GPS_PROVIDER)
    when {
        isLocationUpdateEnabled -> {
            locationViewModel.updateLastLocation()
            showToastMessage(context, MESSAGE_GPS_COMPLETE_DESC)
        }
        else -> showToastMessage(context, MESSAGE_GPS_WARNING)
    }
    return isLocationUpdateEnabled
}
