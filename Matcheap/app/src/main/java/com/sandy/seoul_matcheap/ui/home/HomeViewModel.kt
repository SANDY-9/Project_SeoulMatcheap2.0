package com.sandy.seoul_matcheap.ui.home

import android.location.Location
import androidx.lifecycle.*
import com.sandy.matcheap.common.MESSAGE_ERROR
import com.sandy.matcheap.common.MESSAGE_NETWORK_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.forecast.use_case.GetForecastUseCase
import com.sandy.matcheap.domain.store.use_case.GetStoreListUseCase
import com.sandy.seoul_matcheap.extensions.onIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-02-26
 * @desc
 */

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val getForecastUseCase: GetForecastUseCase,
    private val getStoreListUseCase: GetStoreListUseCase
) : ViewModel() {

    //!-- surrounding stores
    private val _surroundingStoresState = MutableLiveData<SurroundingStoresState>()
    val surroundingStoresState: LiveData<SurroundingStoresState> = _surroundingStoresState
    fun updateSurroundingStoreList(location: Location) {
        onIO {
            getStoreListUseCase.getSurroundingStores(location.latitude, location.longitude).onEach { result ->
                _surroundingStoresState.postValue(
                    when(result) {
                        is Resource.Success -> SurroundingStoresState(data = result.data)
                        is Resource.Error -> SurroundingStoresState(error = result.message ?: MESSAGE_ERROR)
                        is Resource.Loading -> SurroundingStoresState(isLoading = true)
                    }
                )
            }.launchIn(this)
        }
    }

    //!-- random stores
    private val _randomStoreState = MutableLiveData<RandomStoresState>()
    val randomStoreState: LiveData<RandomStoresState> = _randomStoreState
    fun updateRandomStoreList() {
        onIO {
            getStoreListUseCase.getRandomStores().onEach { result ->
                _randomStoreState.postValue(
                    when(result) {
                        is Resource.Success -> RandomStoresState(data = result.data)
                        is Resource.Error -> RandomStoresState(error = result.message ?: MESSAGE_ERROR)
                        is Resource.Loading -> RandomStoresState(isLoading = true)
                    }
                )
            }.launchIn(this)
        }
    }

    //!-- forecast
    private val _forecastState = MutableLiveData<ForecastState>()
    val forecastState: LiveData<ForecastState> = _forecastState
    fun updateForecast(location: Location) {
        onIO {
            getForecastUseCase(location.latitude, location.longitude).onEach { result ->
                _forecastState.postValue(
                    when(result) {
                        is Resource.Success -> ForecastState(data = result.data)
                        is Resource.Error -> ForecastState(error = result.message ?: MESSAGE_NETWORK_ERROR)
                        is Resource.Loading -> ForecastState(isLoading = true)
                    }
                )
            }.launchIn(this)
        }
    }

}
