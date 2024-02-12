package com.sandy.seoul_matcheap.util.helper

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.view.LayoutInflater
import android.view.View
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.*
import com.naver.maps.map.util.MarkerIcons
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.StoreMapItem
import com.sandy.seoul_matcheap.data.store.entity.Polygon
import com.sandy.seoul_matcheap.data.store.entity.StoreInfo
import com.sandy.seoul_matcheap.databinding.ItemMapMarkerBinding
import com.sandy.seoul_matcheap.ui.common.Resource
import javax.inject.Inject
import kotlin.math.sqrt

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-07
 * @desc
 */
class MapUtils @Inject constructor(context: Context) {

    // !-- location Overlay
    private val mapPin = OverlayImage.fromResource(R.drawable.map_pin)
    fun createLocationOverlay(latLng: LatLng, map: NaverMap) = map.locationOverlay.apply {
        position = latLng
        icon = mapPin
        minZoom = MAP_INFO_MAX_ZOOM
        isVisible = true
    }


    // !-- camera
    fun getCameraUpdate(latLng: LatLng, zoom: Double) = CameraUpdate.scrollAndZoomTo(latLng, zoom)
        .animate(CameraAnimation.Easing, CAMERA_ANIMATION_DURATION)


    // !-- marker
    fun createStoreMarker(store: StoreMapItem, hasInfoWindow: Boolean) = InfoWindow().apply {
        position = LatLng(store.lat, store.lng)
        tag = store
        isMaxZoomInclusive = false
        maxZoom = if(hasInfoWindow) MAP_MARKER_MAX_ZOOM else MAP_INFO_MAX_ZOOM
        minZoom = if(hasInfoWindow) MAP_INFO_MAX_ZOOM else MAP_INFO_MIN_ZOOM
    }

    private val markerBinding by lazy { ItemMapMarkerBinding.inflate(LayoutInflater.from(context)) }
    fun getAdapter(store: StoreMapItem, clicked: Boolean, hasInfoWindow: Boolean) = object : InfoWindow.ViewAdapter() {
        override fun getView(p0: InfoWindow): View = getStoreMarkerView(store, clicked, hasInfoWindow)
    }
    private fun getStoreMarkerView(store: StoreMapItem, clicked: Boolean, hasInfoWindow: Boolean) = markerBinding.run {
        with(bgInfo) {
            visibility = if(hasInfoWindow) View.VISIBLE else View.GONE
            backgroundTintList = if(clicked) Resource.matCheapBlue else Resource.matCheapWhite
        }
        with(icBookmark) {
            visibility = if(store.bookmarked) View.VISIBLE else View.GONE
            imageTintList = if(clicked) Resource.matCheapWhite else Resource.matCheapBlue
        }
        with(tvName) {
            setTextColor(if(clicked) Color.WHITE else Resource.matCheapBlack)
            text = store.name
        }
        with(triBg) {
            visibility = if(hasInfoWindow) View.VISIBLE else View.GONE
            imageTintList = if(clicked) Resource.matCheapBlue else Resource.matCheapWhite
        }
        triStroke.visibility = if(hasInfoWindow) View.VISIBLE else View.GONE
        icStore.setImageResource(Resource.getStoreIconDrawableResource(true, store.code))
        with(icShadow) {
            setImageResource(Resource.getStoreIconDrawableResource(true, store.code))
            visibility = if(clicked) View.VISIBLE else View.GONE
        }
        root
    }


    // !-- polygon
    fun createPolygon(gu: String, polygon: List<Polygon>) = PolygonOverlay().apply {
        coords = polygon.map { LatLng(it.lat, it.lng) }
        tag = gu
        outlineWidth = MAP_POLYGON_WIDTH
        color = Resource.colorMatcheapLightYellow
        outlineColor = Color.GRAY
        isMaxZoomInclusive = false
        maxZoom = MAP_INFO_MIN_ZOOM
    }


    // !-- countMarker
    private val circleMarker = OverlayImage.fromResource(R.drawable.ic_count_marker)
    fun createCountMarker(gu: String, count: Int) = Marker().apply {
        position = getPolygonLatLng(gu)
        tag = gu
        icon = circleMarker
        anchor = PointF(MAP_MARKER_ANCHOR, MAP_MARKER_ANCHOR)
        isMaxZoomInclusive = false
        maxZoom = MAP_INFO_MIN_ZOOM

        captionText = "$count"
        captionTextSize = MAP_MARKER_TEXT_SIZE
        captionColor = Resource.colorMatcheapBlue
        setCaptionAligns(Align.Center)
    }

    private val seoulCityHallLatLng = LatLng(SEOUL_CITY_HALL_LAT, SEOUL_CITY_HALL_LNG)
    private fun getPolygonLatLng(gu: String) = when (gu) {
        "종로구" -> LatLng(37.59491732, 126.9773213)
        "중구" -> LatLng(37.56014356, 126.9959681)
        "용산구" -> LatLng(37.53138497, 126.979907)
        "성동구" -> LatLng(37.55102969, 127.0410585)
        "광진구" -> LatLng(37.54670608, 127.0857435)
        "동대문구" -> LatLng(37.58195655, 127.0548481)
        "중랑구" -> LatLng(37.59780259, 127.0928803)
        "성북구" -> LatLng(37.6057019, 127.0175795)
        "강북구" -> LatLng(37.64347391, 127.011189)
        "도봉구" -> LatLng(37.66910208, 127.0323688)
        "노원구" -> LatLng(37.65251105, 127.0750347)
        "은평구" -> LatLng(37.61921128, 126.9270229)
        "서대문구" -> LatLng(37.57778531, 126.9390631)
        "마포구" -> LatLng(37.55931349, 126.90827)
        "양천구" -> LatLng(37.52478941, 126.8554777)
        "강서구" -> LatLng(37.56123543, 126.822807)
        "구로구" -> LatLng(37.49440543, 126.8563006)
        "금천구" -> LatLng(37.46056756, 126.9008202)
        "영등포구" -> LatLng(37.52230829, 126.9101695)
        "동작구" -> LatLng(37.49887688, 126.9516415)
        "관악구" -> LatLng(37.46737569, 126.9453372)
        "서초구" -> LatLng(37.47329547, 127.0312203)
        "강남구" -> LatLng(37.49664389, 127.0629852)
        "송파구" -> LatLng(37.50561924, 127.115295)
        "강동구" -> LatLng(37.55045024, 127.1470118)
        else -> seoulCityHallLatLng
    }

    fun getCenterLatLng(gu: String) = when (gu) {
        "종로구" -> LatLng(37.5733791, 126.9790155)
        "노원구" -> LatLng(37.6542584, 127.0419498)
        "동작구" -> LatLng(37.512434, 126.937611)
        "영등포구" -> LatLng(37.528640, 126.894574)
        "서대문구" -> LatLng(37.579177, 126.9345928)
        "광진구" -> LatLng(37.5385375, 127.0801885)
        "용산구" -> LatLng(37.532452666, 126.990646275)
        "중구" -> LatLng(37.5638235, 126.9976090)
        "송파구" -> LatLng(37.51468273, 127.10602247)
        "중랑구" -> LatLng(37.6060713, 127.0916155)
        "구로구" -> LatLng(37.4954745, 126.8854504)
        "마포구" -> LatLng(37.5659701, 126.9020941)
        "강북구" -> LatLng(37.6395693, 127.0256484)
        "강남구" -> LatLng(37.5177437, 127.047319)
        "양천구" -> LatLng(37.516955, 126.8643757)
        "금천구" -> LatLng(37.4567709, 126.8932118)
        "도봉구" -> LatLng(37.6662953, 126.9948531)
        "성북구" -> LatLng(37.58935432, 127.0166185)
        "동대문구" -> LatLng(37.5744197, 127.037554)
        "은평구" -> LatLng(37.6026621, 126.9291908)
        "관악구" -> LatLng(37.4781327, 126.9493137)
        "강서구" -> LatLng(37.5514337, 126.8496375)
        "서초구" -> LatLng(37.4835782, 127.0304723)
        "강동구" -> LatLng(37.5302997, 127.1210551)
        "성동구" -> LatLng(37.5632323, 127.0364314)
        else -> seoulCityHallLatLng
    }


    // !-- circle overlay
    fun createCircleOverlay(centerX: Double, centerY: Double, r: Double) = CircleOverlay().apply {
        center = LatLng(centerX, centerY)
        color = Resource.colorMatcheapTransparentBlue
        radius = sqrt(r) * 100000
        minZoom = MAP_INFO_MIN_ZOOM
    }


    fun createStoreLocationMarker(store: StoreInfo, map: NaverMap) = Marker().apply {
        position = LatLng(store.lat, store.lng)
        width = STORE_LOCATION_MARKER_WIDTH
        height = STORE_LOCATION_MARKER_HEIGHT
        icon = MarkerIcons.BLACK
        iconTintColor = Resource.colorMatcheapBlue
        captionText = store.name
        captionColor = Resource.colorMatcheapBlue
        captionTextSize = MARKER_CAPTION_TEXT_SIZE
        captionOffset = MARKER_CAPTION_TEXT_OFFSET
        isHideCollidedSymbols = true
        this.map = map
    }


    companion object {

        // !-- camera
        const val MAP_DEFAULT_ZOOM = 14.0
        private const val CAMERA_ANIMATION_DURATION = 750L

        // !-- marker
        private const val MAP_MARKER_MAX_ZOOM = 21.0
        const val MAP_INFO_MAX_ZOOM = 14.0
        const val MAP_INFO_MIN_ZOOM = 13.0

        // !-- countMarker
        private const val MAP_MARKER_ANCHOR = 0.5f
        private const val MAP_MARKER_TEXT_SIZE = 22f

        private const val MAP_POLYGON_WIDTH = 3

        const val Z_INDEX_DEFAULT = 0
        const val Z_INDEX_CLICKED = 1

        // !-- store location marker
        const val STORE_LOCATION_MARKER_WIDTH = 64
        const val STORE_LOCATION_MARKER_HEIGHT = 85
        const val MARKER_CAPTION_TEXT_SIZE = 14f
        const val MARKER_CAPTION_TEXT_OFFSET = 7


        const val SEOUL_CITY_HALL_LAT = 37.5290446
        const val SEOUL_CITY_HALL_LNG = 126.9439883
    }
}