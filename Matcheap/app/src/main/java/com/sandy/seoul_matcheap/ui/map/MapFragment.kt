package com.sandy.seoul_matcheap.ui.map

import android.content.*
import android.location.*
import android.net.ConnectivityManager
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.*
import com.sandy.seoul_matcheap.MatcheapApplication.Companion.showToastMessage
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.*
import com.sandy.seoul_matcheap.databinding.*
import com.sandy.seoul_matcheap.extension.updateLocation
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.common.*
import com.sandy.seoul_matcheap.ui.more.bookmark.BookmarkViewModel
import com.sandy.seoul_matcheap.ui.store.StoreDetailsActivity
import com.sandy.seoul_matcheap.util.constants.*
import com.sandy.seoul_matcheap.util.helper.MapUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map) {


    @Inject lateinit var mapUtils: MapUtils
    private val mapViewModel: MapViewModel by activityViewModels()

    @Inject lateinit var locationManager: LocationManager
    private val locationViewModel: LocationViewModel by viewModels()

    override fun setupBinding(): FragmentMapBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@MapFragment
            location = locationViewModel
            zoom = MapUtils.MAP_DEFAULT_ZOOM
        }
    }

    override fun downloadData() {
        val isLocationUpdateEnabled = updateLocation()
        if(!isLocationUpdateEnabled) mapViewModel.updateData(MapUtils.SEOUL_CITY_HALL_LAT, MapUtils.SEOUL_CITY_HALL_LNG)
    }

    fun updateLocation() = locationManager.updateLocation(locationViewModel, requireContext())

    override fun initView() {
        initMapAsync()
    }

    private fun initMapAsync() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.naverMap) as MapFragment
        mapFragment.getMapAsync {
            binding.map = it
            it.onLoad()
            subscribeToObservers(it)
        }
    }

    private fun NaverMap.onLoad() = addOnLoadListener {
        completeProgress()
        addOnZoomChangedListener()
        addOnMapTouchEventLister()
    }

    private fun completeProgress() = binding.run {
        progressView.visibility = View.GONE
        animationView.cancelAnimation()
    }

    private fun NaverMap.addOnZoomChangedListener() = addOnCameraIdleListener {
        val zoom = cameraPosition.zoom
        binding.zoom = zoom
        if(zoom >= MapUtils.MAP_INFO_MAX_ZOOM) {
            val target = cameraPosition.target
            locationViewModel.updateAddress(target.latitude, target.longitude)
        }
    }

    private fun NaverMap.addOnMapTouchEventLister() {
        setOnMapDoubleTapListener { _, _ -> updateLocation() }
        setOnMapClickListener { _, _ -> closeStoreBottomSheet() }
    }


    private fun subscribeToObservers(map: NaverMap) {
        subscribeToLocationObserver(map)
        subscribeToMapOverlays(map)
        subscribeToBookmarkObserver(map)
    }

    private fun subscribeToLocationObserver(map: NaverMap) {
        locationViewModel.location.observe(viewLifecycleOwner) {
            handleLocationUpdate(it, map)
        }
    }

    private fun handleLocationUpdate(location: Location?, map: NaverMap) = location?.run {
        val latLng = LatLng(latitude, longitude)
        mapViewModel.updateData(centerX = latitude, centerY = longitude)
        mapUtils.createLocationOverlay(latLng, map)
        map.updateCamera(latLng, MapUtils.MAP_DEFAULT_ZOOM)
    }

    private fun subscribeToMapOverlays(map: NaverMap) = mapViewModel.run {
        storeMarkers.observe(viewLifecycleOwner) {
            map.createStoreMarkers(it)
        }
        polygonOverlays.observe(viewLifecycleOwner) {
            map.createPolygonOverlays(it)
        }
        countMarkers.observe(viewLifecycleOwner) {
            map.createCountMarkers(it)
        }
        rangeCircleOverlay.observe(viewLifecycleOwner) {
            map.createRangeCircleOverlay(it)
        }
    }

    //!-- create Store Markers
    private fun NaverMap.createStoreMarkers(markers: Map<InfoWindow, InfoWindow>) = lifecycleScope.launch {
        val map = this@createStoreMarkers
        markers.forEach { (has, not) ->
            has.setStoreMarker(true, map)
            not.setStoreMarker(false, map)
        }
    }

    private fun InfoWindow.setStoreMarker(hasInfoWindow: Boolean, map: NaverMap) {
        addOnMarkerClickListener()
        adapter = mapUtils.getAdapter(tag as StoreMapItem, false, hasInfoWindow)
        this.map = map
    }

    private var clickedMarkerWithInfo: InfoWindow? = null
    private var clickedMarkerNoInfo: InfoWindow? = null
    private fun InfoWindow.addOnMarkerClickListener() = setOnClickListener {
        // NaverMap.setOnMapTouchListener is delivered click event by returning false from Overlay. not allow re-settings.
        if(clickedMarkerWithInfo == it || clickedMarkerNoInfo == it) return@setOnClickListener false
        openStoreBottomSheet(tag as StoreMapItem)
        updateClickedMarker(this)
        true
    }

    private fun updateClickedMarker(newClickMarker: InfoWindow?) {
        clickedMarkerWithInfo?.setClickState(clicked = false, hasInfoWindow = true)
        clickedMarkerNoInfo?.setClickState(clicked = false, hasInfoWindow = false)

        val markerPair = newClickMarker?.let { mapViewModel.getStoreMarkerPair(it) }
        clickedMarkerWithInfo = markerPair?.first?.setClickState(clicked = true, hasInfoWindow = true)
        clickedMarkerNoInfo = markerPair?.second?.setClickState(clicked = true, hasInfoWindow = true)
    }

    private fun InfoWindow.setClickState(clicked: Boolean, hasInfoWindow: Boolean, store: StoreMapItem? = null) = this.apply {
        val store = store ?: tag as StoreMapItem
        tag = store
        adapter = mapUtils.getAdapter(store, clicked, hasInfoWindow)
        zIndex = if (clicked) MapUtils.Z_INDEX_CLICKED else MapUtils.Z_INDEX_DEFAULT
    }


    //!-- create Polygon Overlays
    private fun NaverMap.createPolygonOverlays(polygons: Map<String, PolygonOverlay>) = polygons.forEach { (_, polygon)->
        polygon.map = this
        polygon.setOnClickListener { polygon.onPolygonClick(this) }
    }

    private fun PolygonOverlay.onPolygonClick(map: NaverMap) : Boolean {
        color = Resource.colorMatcheapLightGray
        updateMap(map)
        registerHandler(delay = 1000L) { color = Resource.colorMatcheapLightYellow }
        return true
    }

    private fun PolygonOverlay.updateMap(map: NaverMap) {
        val center = mapUtils.getCenterLatLng(tag as String)
        mapViewModel.updateData(centerX = center.latitude, centerY = center.longitude)
        map.updateCamera(center, MapUtils.MAP_DEFAULT_ZOOM)
    }

    private fun NaverMap.updateCamera(latLng: LatLng, zoom: Double) {
        closeStoreBottomSheet()
        val cameraUpdate = mapUtils.getCameraUpdate(latLng, zoom)
        moveCamera(cameraUpdate)
    }


    //!-- create Count Markers
    private fun NaverMap.createCountMarkers(markers: Map<String, Marker>) = markers.forEach { (_,marker)->
        marker.map = this
        marker.setOnClickListener {
            val pairPolygon = mapViewModel.getPairPolygon(it.tag as String)
            pairPolygon.onPolygonClick(this)
        }
    }


    //!-- create Range Circle Overlay
    private fun NaverMap.createRangeCircleOverlay(circleOverlay: CircleOverlay?) = circleOverlay?.let {
        it.map = this
    }


    //!--infoBottomSheet
    private fun openStoreBottomSheet(item: StoreMapItem) = binding.run {
        bottomSheet.store = item
        mapFragment.startTransition(R.id.open_transition)
    }

    private fun closeStoreBottomSheet() = clickedMarkerWithInfo?.let {
        binding.mapFragment.startTransition(R.id.close_transition)
        updateClickedMarker(null)
    }

    //!-- bookmark
    @Inject lateinit var connectivityManager: ConnectivityManager
    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    fun updateBookmarkState(store: StoreMapItem, isChecked: Boolean) = lifecycleScope.launch {
        connectivityManager.activeNetwork?.let {
            store.bookmarked = isChecked
            updateClickedMarker(store)
            bookmarkViewModel.updateBookmark(store.id, store.code, isChecked)
        } ?: cancelBookmarkUpdate()
    }

    private fun updateClickedMarker(store: StoreMapItem) {
        clickedMarkerWithInfo?.setClickState(clicked = true, hasInfoWindow = true, store)
        clickedMarkerNoInfo?.setClickState(clicked = true, hasInfoWindow = true, store)
    }

    private fun cancelBookmarkUpdate() = binding.bottomSheet.apply {
        showToastMessage(requireContext(), MESSAGE_NETWORK_ERROR)
        btnBookmark.isChecked = false
        store?.run {
            bookmarked = false
            updateClickedMarker(this)
        }
    }


    // !-- request re-search
    fun onReSearch(map: NaverMap) {
        val center = map.cameraPosition.target
        mapViewModel.updateData(centerX = center.latitude, centerY = center.longitude)
        showToastMessage(requireContext(), MESSAGE_MAP_DESC)
    }


    private fun subscribeToBookmarkObserver(map: NaverMap) {
        bookmarkViewModel.loadingState.observe(viewLifecycleOwner) {
            if(it == ConnectState.FAIL) { cancelBookmarkUpdate() }
        }
    }

    override fun navigateToStoreDetails(id: String) {
        val intent = Intent(requireContext(), StoreDetailsActivity::class.java).apply {
            putExtra(STORE_ID, id)
        }
        storeDetailsActivityLauncher.launch(intent)
    }
    private val storeDetailsActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { updateStore(it.data) }

    // when back from StoreDetailsActivity, update current clicked store because bookmark state could change.
    @Suppress("DEPRECATION")
    private fun updateStore(intent: Intent?) = intent?.run {
        lifecycleScope.launch {
            val store = getSerializableExtra(STORE_) as StoreMapItem
            updateClickedMarker(store)
            binding.bottomSheet.store = store
        }
    }

    override fun setOnBackPressedListener() {
        handleExistDeepLinkNavigation()
    }

    //!-- filter
    fun openMapFilter() {
        closeStoreBottomSheet()
        requireActivity().supportFragmentManager.setFragmentResult(FILTER_OPEN, bundleOf(FILTER_OPEN to true))
    }

    override fun destroyGlobalVariables() {
        mapViewModel.onCleared()
        clickedMarkerWithInfo = null
        clickedMarkerNoInfo = null
        requireActivity().supportFragmentManager.setFragmentResult(FILTER_OPEN, bundleOf(FILTER_OPEN to false))
    }

}