package com.sandy.seoul_matcheap.ui.splash

import androidx.lifecycle.*
import com.sandy.seoul_matcheap.data.store.entity.Polygon
import com.sandy.seoul_matcheap.data.store.entity.StoreInfo
import com.sandy.seoul_matcheap.data.store.repository.MapRepository
import com.sandy.seoul_matcheap.data.store.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-17
 * @desc
 */

@HiltViewModel
class LoadViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val mapRepository: MapRepository
    ) : ViewModel() {

    private val storeDataLoadState = MutableLiveData(false)
    private val polygonDataLoadState = MutableLiveData(false)

    private val _dataLoadState = MediatorLiveData<Boolean>().apply {
        addSource(storeDataLoadState) {
            value = getLoadState()
        }
        addSource(polygonDataLoadState) {
            value = getLoadState()
        }
    }
    val dataLoadSate : LiveData<Boolean> = _dataLoadState
    private fun getLoadState() = storeDataLoadState.value!! && polygonDataLoadState.value!!
    fun setLoadState(state: Boolean) {
        storeDataLoadState.value = state
        polygonDataLoadState.value = state
    }

    fun updateDatabase(storeList: List<String>, polygonList: List<String>) {
        fetchStoresData(storeList)
        fetchPolygonsData(polygonList)
    }

    // database fetch : insert store data
    private fun fetchStoresData(stores: List<String>) = viewModelScope.launch(Dispatchers.IO) {
        val downloadedStores = stores.map {
            val token = it.split("\t")
            val lat = token[12].trim().toDouble()
            val lng = token[13].trim().toDouble()
            StoreInfo(
                id = "0000" + token[0].trim(),
                code = token[1].trim(),
                name = token[2].trim(),
                gu = token[3].trim(),
                address = token[4].trim(),
                tel = token[5].trim(),
                time = token[6].trim(),
                closed = token[7].trim(),
                reserve = token[8].trim(),
                parking = token[9].trim(),
                content = token[10].trim(),
                photo = token[11].trim(),
                lat = lat,
                lng = lng
            )
        }
        storeRepository.insertStores(downloadedStores)
        storeDataLoadState.postValue(true)
    }

    // database fetch : insert polygon data
    private fun fetchPolygonsData(polygons: List<String>) = viewModelScope.launch(Dispatchers.IO) {
        val downloadedPolygons = polygons.mapIndexed { index, s ->
            val token = s.split("\t")
            Polygon(
                num = index + 1,
                gu = token[0].trim(),
                lat = token[1].trim().toDouble(),
                lng = token[2].trim().toDouble()
            )
        }
        mapRepository.insertPolygons(downloadedPolygons)
        polygonDataLoadState.postValue(true)
    }

}