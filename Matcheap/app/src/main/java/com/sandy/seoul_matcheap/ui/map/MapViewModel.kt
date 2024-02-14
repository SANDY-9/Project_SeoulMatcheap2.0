package com.sandy.seoul_matcheap.ui.map

import androidx.lifecycle.*
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolygonOverlay
import com.sandy.seoul_matcheap.data.store.dao.DistanceRadius
import com.sandy.seoul_matcheap.data.store.repository.MapRepository
import com.sandy.seoul_matcheap.util.constants.ALL_SELECT
import com.sandy.seoul_matcheap.util.constants.Category
import com.sandy.seoul_matcheap.util.helper.MapUtils
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
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val mapUtils: MapUtils
    ) : ViewModel() {

    private var code: List<String> = Category.all()
    private var gu: String? = null
    private var bookmarked = false
    private var centerX: Double = 0.0
    private var centerY: Double = 0.0
    private var r: Double? = null

    fun filterCode(filtered: Boolean, param: String? = null) {
        this.code = when(param) {
            null -> if(filtered) Category.all() else listOf()
            else -> code.toMutableList().apply {
                if(filtered) {
                    if(code.size == 8) clear()
                    add(param)
                } else remove(param)
            }
        }
        updateData(centerX = centerX, centerY = centerY)
    }

    fun filterGu(param: String) {
        this.gu = if(param != ALL_SELECT) param else null
        updateData(centerX = centerX, centerY = centerY)
    }

    fun filterBookmark(param: Boolean) {
        this.bookmarked = param
        updateData(centerX = centerX, centerY = centerY)
    }

    fun filterDistance(
        param: Double?,
        centerX: Double,
        centerY: Double
    ) {
        if(param == null && r == null) return
        this.r = param
        updateData(centerX = centerX, centerY = centerY)
        updateRangeCircleOverlay()
    }

    private fun initData() {
        this.code = Category.all()
        this.gu = null
        this.bookmarked = false
        this.r = null
        updateRangeCircleOverlay()
    }

    fun resetFilter() {
        if(code.size == 8 && gu == null && !bookmarked && r == null) return
        initData()
        updateData(centerX = centerX, centerY = centerY)
    }


    private val _storeMarkers = MutableLiveData<MutableMap<InfoWindow, InfoWindow>>()
    val storeMarkers: LiveData<MutableMap<InfoWindow, InfoWindow>> = _storeMarkers

    fun getStoreMarkerPair(marker: InfoWindow) = storeMarkers.value!!.let {
        when(val noInfo = it[marker]) {
            null -> it.filterValues { value-> value == marker }.keys.single() to marker
            else -> marker to noInfo
        }
    }

    private fun createStoreMarkers() = viewModelScope.launch(Dispatchers.IO) {
        val stores = mapRepository.downloadStoresByFilter(
            code = code,
            gu = gu,
            bookmarked = bookmarked,
            centerX = centerX,
            centerY = centerY,
            r = r ?: DistanceRadius.M2500.value
        )
        val markers = stores.associate{
            mapUtils.createStoreMarker(it, true) to mapUtils.createStoreMarker(it, false)
        }.toMutableMap()
        _storeMarkers.postValue(markers)
    }

    private fun updateStoreMarkers() = viewModelScope.launch {
        storeMarkers.value?.onEach { (has, no) ->
            has.map = null
            no.map = null
        }.also {
            it?.clear()
            createStoreMarkers()
        }
    }


    private val _polygonOverlays = MutableLiveData<MutableMap<String, PolygonOverlay>>()
    val polygonOverlays: LiveData<MutableMap<String, PolygonOverlay>> = _polygonOverlays
    fun getPairPolygon(gu: String) = polygonOverlays.value!![gu]!!
    private fun createPolygonOverlays() = viewModelScope.launch(Dispatchers.IO) {
        val polygonOverlays = mapRepository.downloadPolygons().mapValues { (gu, polygons) ->
            mapUtils.createPolygon(gu, polygons)
        }.toMutableMap()
        _polygonOverlays.postValue(polygonOverlays)
    }


    private val _countMarkers = MutableLiveData<MutableMap<String, Marker>>()
    val countMarkers: LiveData<MutableMap<String, Marker>> = _countMarkers

    private fun createCountMarkers() = viewModelScope.launch(Dispatchers.IO) {
        val countsMarkers = mapRepository.downloadStoreCountByGu(
            code = code,
            gu = gu,
            bookmarked = bookmarked,
            centerX = centerX,
            centerY = centerY,
            r = r
        ).mapValues {(gu, count) ->
            mapUtils.createCountMarker(gu, count)
        }.toMutableMap()
        _countMarkers.postValue(countsMarkers)
    }

    private fun updateCountMarkers() = viewModelScope.launch {
        countMarkers.value?.onEach { (_, marker) ->
            marker.map = null
        }.also {
            it?.clear()
            createCountMarkers()
        }
    }


    private val _rangeCircleOverlay  = MutableLiveData<CircleOverlay?>(null)
    val rangeCircleOverlay: LiveData<CircleOverlay?> = _rangeCircleOverlay

    private fun createRangeCircleOverlay() = r?.let {
        val circleOverlay = mapUtils.createCircleOverlay(centerX, centerY, it)
        _rangeCircleOverlay.postValue(circleOverlay)
    }


    private fun updateRangeCircleOverlay() = rangeCircleOverlay.value?.let {
        it.map = null
    }.also {
        createRangeCircleOverlay()
    }


    fun updateData(
        centerX: Double,
        centerY: Double
    ) {
        if(this.r == null) {
            this.centerX = centerX
            this.centerY = centerY
        }
        updateStoreMarkers()
        updateCountMarkers()
    }

    init {
        createPolygonOverlays()
    }

    private fun clearMarkers() {
        viewModelScope.launch {
            storeMarkers.value?.forEach { (u, i) ->
                u.map = null
                i.map = null
            }
        }
        viewModelScope.launch {
            polygonOverlays.value?.forEach { (_, polygonOverlay) ->
                polygonOverlay.map = null
            }
        }
        viewModelScope.launch {
            countMarkers.value?.forEach { (_, marker) ->
                marker.map = null
            }
        }
        rangeCircleOverlay.value?.map = null
    }

    public override fun onCleared() {
        initData()
        clearMarkers()
        super.onCleared()
    }

}