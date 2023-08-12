package com.sandy.seoul_matcheap.ui.map

import androidx.lifecycle.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolygonOverlay
import com.sandy.seoul_matcheap.data.store.repository.MapRepository
import com.sandy.seoul_matcheap.util.constants.ALL_SELECT
import com.sandy.seoul_matcheap.util.constants.NOT_DISTANCE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-17
 * @desc
 */

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val mapUtils: MapUtils
    ) : ViewModel() {

    private val _storeMarkers = MutableLiveData<Pair<List<InfoWindow>, List<InfoWindow>>?>()
    val storeMarkers: LiveData<Pair<List<InfoWindow>, List<InfoWindow>>?> = _storeMarkers
    val createdStoreMarkers: Pair<List<InfoWindow>, List<InfoWindow>> get() = storeMarkers.value ?: Pair(listOf(), listOf())
    fun getStoreMarkerPair(marker: InfoWindow) : List<InfoWindow>? {
        val (hasInfoWindow, hasNotInfoWindow) = createdStoreMarkers
        val index = if(hasInfoWindow.contains(marker)) hasInfoWindow.indexOf(marker) else hasNotInfoWindow.indexOf(marker)
        return if(index > -1) listOf(hasInfoWindow[index], hasNotInfoWindow[index]) else null
    }

    private val _polygonOverlays = MutableLiveData<List<PolygonOverlay>?>()
    val polygonOverlays: LiveData<List<PolygonOverlay>?> = _polygonOverlays
    val createdPolygon: Map<String, PolygonOverlay> get() = polygonOverlays.value!!.associateBy { it.tag as String }

    private val _countMarkers = MutableLiveData<List<Marker>?>()
    val countMarkers: LiveData<List<Marker>?> = _countMarkers
    val createdCountMarkers: Map<String, Marker> get() = countMarkers.value!!.associateBy { it.tag as String }

    fun downloadMapData() = viewModelScope.launch(Dispatchers.IO) {
        createStoreMarkers()
        createPolygonOverlays()
        createCountMarkers()
    }
    private suspend fun createStoreMarkers() {
        val stores = mapRepository.downloadStores()
        val markers = stores.map {
            mapUtils.createStoreMarker(it, true) to mapUtils.createStoreMarker(it, false)
        }.unzip()
        _storeMarkers.postValue(markers)
    }
    private suspend fun createPolygonOverlays() {
        val polygons = mapRepository.downloadPolygons()
        val polygonOverlays = polygons.map {
            mapUtils.createPolygon(it.key, it.value)
        }
        _polygonOverlays.postValue(polygonOverlays)
    }
    private suspend fun createCountMarkers() {
        val counts = mapRepository.downloadStoreCountForGu()
        val countMarkers = counts.map {
            mapUtils.createCountMarker(it)
        }
        _countMarkers.postValue(countMarkers)
    }


    private val _filter = MutableLiveData<Filter?>()
    val filter: LiveData<Filter?> = _filter
    fun updateFilter(filter: Filter) {
        _filter.postValue(filter)
    }

    fun init() {
        _filter.value = null
        _storeMarkers.value = null
        _polygonOverlays.value = null
        _countMarkers.value = null
        super.onCleared()
    }

}

data class Filter(
    val region: String = ALL_SELECT,
    val category: Set<String>,
    val distance: Double = NOT_DISTANCE,
    val bookmarked: Boolean = false
) : Serializable
