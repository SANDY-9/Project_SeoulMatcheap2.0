package com.sandy.seoul_matcheap.ui.splash

import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.*
import com.sandy.seoul_matcheap.data.store.entity.Polygon
import com.sandy.seoul_matcheap.data.store.entity.StoreInfo
import com.sandy.seoul_matcheap.data.store.repository.MapRepository
import com.sandy.seoul_matcheap.data.store.repository.StoreRepository
import com.sandy.seoul_matcheap.util.constants.APP_PREFS_SETTINGS
import com.sandy.seoul_matcheap.util.helper.AppPrefsUtils
import com.sandy.seoul_matcheap.util.helper.DataHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-17
 * @desc
 */

@HiltViewModel
class LoadViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val mapRepository: MapRepository,
    @Named(APP_PREFS_SETTINGS) private val prefs: SharedPreferences
    ) : ViewModel() {

    private val storeDataLoadState = MutableLiveData(false)
    private val polygonDataLoadState = MutableLiveData(false)

    private val _dataLoadState = MediatorLiveData<Boolean>().apply {
        addSource(storeDataLoadState) {
            value = getState()
        }
        addSource(polygonDataLoadState) {
            value = getState()
        }
    }
    val dataLoadSate : LiveData<Boolean> = _dataLoadState
    private fun getState() = storeDataLoadState.value!! && polygonDataLoadState.value!!

    fun updateDatabase(location: Location, storeList: List<String>, polygonList: List<String>?) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchStoresData(location, storeList)
        }
        polygonList?.let {
            viewModelScope.launch(Dispatchers.IO) {
                fetchPolygonsData(it)
            }
        } ?: polygonDataLoadState.postValue(true)
    }

    // database fetch : insert store data
    private suspend fun fetchStoresData(location: Location, stores: List<String>){
        val downloadedStores = stores.map {
            val token = it.split("\t")
            val lat = token[13].trim().toDouble()
            val lng = token[14].trim().toDouble()
            val distance = DataHelper.calculateDistance(
                curLat = location.latitude,
                curLng = location.longitude,
                lat = lat,
                lng = lng
            )
            StoreInfo(
                id = "0000" + token[0].trim(),
                code = token[1].trim(),
                codeName = token[2].trim(),
                name = token[3].trim(),
                gu = token[4].trim(),
                address = token[5].trim(),
                tel = token[6].trim(),
                time = token[7].trim(),
                closed = token[8].trim(),
                reserve = token[9].trim(),
                parking = token[10].trim(),
                content = token[11].trim(),
                photo = token[12].trim(),
                lat = lat,
                lng = lng,
                distance = distance
            )
        }
        storeRepository.insertStores(downloadedStores)
        storeDataLoadState.postValue(true)
    }

    // database fetch : insert polygon data
    private suspend fun fetchPolygonsData(polygons: List<String>) {
        val downloadedPolygons = polygons.map {
            val token = it.split("\t")
            Polygon(
                gu = token[0].trim(),
                lat = token[1].trim().toDouble(),
                lng = token[2].trim().toDouble()
            )
        }
        mapRepository.insertPolygons(downloadedPolygons)
        AppPrefsUtils.setDatabaseState(prefs)
        polygonDataLoadState.postValue(true)
    }

}