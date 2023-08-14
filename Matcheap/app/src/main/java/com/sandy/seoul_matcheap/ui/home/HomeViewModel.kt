package com.sandy.seoul_matcheap.ui.home

import android.location.Location
import androidx.lifecycle.*
import com.sandy.seoul_matcheap.data.forecast.Forecast
import com.sandy.seoul_matcheap.data.forecast.ForecastRepository
import com.sandy.seoul_matcheap.data.store.dao.RandomStore
import com.sandy.seoul_matcheap.data.store.dao.SurroundingStore
import com.sandy.seoul_matcheap.data.store.repository.StoreRepository
import com.sandy.seoul_matcheap.util.constants.ConnectState
import com.sandy.seoul_matcheap.util.constants.DEFAULT_
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.ln

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-02-26
 * @desc
 */

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val storeRepository: StoreRepository,
    private val forecastRepository: ForecastRepository
) : ViewModel() {

    //!-- surrounding stores
    private val _surroundingStores = MutableLiveData<List<SurroundingStore>>()
    val surroundingStores: LiveData<List<SurroundingStore>> = _surroundingStores
    fun updateSurroundingStoreList(location: Location) = viewModelScope.launch(Dispatchers.IO) {
        val stores = storeRepository.downloadSurroundingStores(location)
        _surroundingStores.postValue(stores)
    }

    //!-- random stores
    private val _randomStore = MutableLiveData<List<RandomStore>>()
    val randomStore: LiveData<List<RandomStore>> = _randomStore
    fun updateRandomStoreList() = viewModelScope.launch(Dispatchers.IO) {
        val stores = storeRepository.downloadRandomStores()
        _randomStore.postValue(stores)
    }


    private val _loadingState = MutableLiveData(ConnectState.NONE)
    val loadingState : LiveData<ConnectState> = _loadingState
    fun setLoadingState(state: ConnectState) {
        _loadingState.postValue(state)
    }


    //!-- forecast
    private val _temperature = MutableLiveData<String>()
    val temperature : LiveData<String> = _temperature
    private val _pty = MutableLiveData(DEFAULT_)
    val pty : LiveData<String> = _pty
    private val _sky = MutableLiveData<String>()
    val sky : LiveData<String> = _sky
    private val _wind = MutableLiveData<String>()
    val wind : LiveData<String> = _wind
    fun updateForecast(location: Location) = viewModelScope.launch(Dispatchers.IO) {
        val result = forecastRepository.downloadCurrentForecast(location.latitude, location.longitude)
        updateWeather(result)
    }

    private fun updateWeather(result: List<Forecast>) = when {
        result.isEmpty() -> setLoadingState(ConnectState.FAIL)
        else -> {
            setWeather(result)
            setLoadingState(ConnectState.SUCCESS)
        }
    }

    private fun setWeather(result: List<Forecast>) = result.forEach {
        when (it.category.trim()) {
            T1H -> _temperature.postValue(it.fcstValue)
            PTY -> _pty.postValue(it.fcstValue)
            SKY -> _sky.postValue(it.fcstValue)
            WSD -> _wind.postValue(it.fcstValue)
        }
    }

    private companion object {
        const val T1H = "T1H"
        const val SKY = "SKY"
        const val PTY = "PTY"
        const val WSD = "WSD"
    }

}
