package com.sandy.seoul_matcheap.ui

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Looper
import androidx.lifecycle.*
import com.google.android.gms.location.*
import com.google.android.gms.tasks.*
import com.sandy.seoul_matcheap.util.*
import com.sandy.seoul_matcheap.util.constants.LOCATION_UPDATE_INTERVAL
import com.sandy.seoul_matcheap.util.constants.SEOUL_CITY_ADDRESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-17
 * @desc
 */

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
    ) : ViewModel() {

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.lastLocation?.let {
                _location.postValue(it)
                updateAddress(it.latitude, it.longitude)
            }
        }
    }
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        LOCATION_UPDATE_INTERVAL
    ).build()
    @SuppressLint("MissingPermission")
    fun updateLocation() = fusedLocationProviderClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
    fun stopLocation() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    private val currentLocationRequest = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .build()
    private val cancellationToken = object : CancellationToken() {
        override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
        override fun isCancellationRequested(): Boolean = false
    }
    @SuppressLint("MissingPermission")
    fun getLastLocation() = fusedLocationProviderClient.getCurrentLocation(
        currentLocationRequest,
        cancellationToken
    ).addOnSuccessListener {
        _location.postValue(it)
        updateAddress(it.latitude, it.longitude)
    }


    private val _address = MutableLiveData(MESSAGE_INIT_ADDRESS)
    val address: LiveData<String> = _address
    fun getCurrentAddress() = address.value ?: SEOUL_CITY_ADDRESS
    private fun setAddress(address: String) {
        _address.postValue(address)
    }
    fun updateAddress(lat: Double, lng: Double) = viewModelScope.launch(Dispatchers.IO) {
        setAddress(MESSAGE_INIT_ADDRESS)
        try {
            when {
                Build.VERSION.SDK_INT >= TIRAMISU -> {
                    geocoder.getFromLocation(lat, lng, ADDRESS_MAX_LINES) {
                        val address = getAddress(it)
                        setAddress(address)
                    }
                }
                else -> {
                    @Suppress("DEPRECATION")
                    val addressLine = geocoder.getFromLocation(lat, lng, ADDRESS_MAX_LINES)!!
                    val address = getAddress(addressLine)
                    setAddress(address)
                }
            }
        } catch (e: Exception) {
            setAddress(MESSAGE_ERROR_ADDRESS)
        }
    }

    private fun getAddress(address: List<Address>) : String {
        val split = address.first().getAddressLine(0).split(" ")
        return "${split[1]} ${split[2]} ${split[3]}"
    }

    fun init() {
        _location.value = null
    }

    companion object {
        const val MESSAGE_INIT_ADDRESS = "주소 정보 가져 오는 중..."
        private const val MESSAGE_ERROR_ADDRESS = "주소 정보를 얻을 수 없습니다."
        private const val ADDRESS_MAX_LINES = 3
    }

}