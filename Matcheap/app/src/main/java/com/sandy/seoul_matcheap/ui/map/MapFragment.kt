package com.sandy.seoul_matcheap.ui.map

import android.content.*
import android.graphics.*
import android.location.*
import android.net.ConnectivityManager
import android.os.*
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.*
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.*
import com.sandy.seoul_matcheap.data.store.entity.*
import com.sandy.seoul_matcheap.databinding.*
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.common.*
import com.sandy.seoul_matcheap.ui.more.bookmark.BookmarkViewModel
import com.sandy.seoul_matcheap.ui.store.StoreDetailsActivity
import com.sandy.seoul_matcheap.util.constants.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map) {

    private val locationViewModel: LocationViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()
    private val bookmarkViewModel: BookmarkViewModel by viewModels()

    override fun setupBinding(): FragmentMapBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@MapFragment
            mapViewModel = this@MapFragment.mapViewModel
            locationViewModel = this@MapFragment.locationViewModel
            zoom = MapUtils.MAP_DEFAULT_ZOOM
        }
    }

    @Inject lateinit var locationManager: LocationManager
    override fun downloadData() {
        updateLocation(locationViewModel, locationManager)
        mapViewModel.downloadMapData()
    }

    override fun initView() {
        initMap()
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.naverMap) as MapFragment
        mapFragment.getMapAsync {
            val map = getMap(it)
            subscribeToLocationObserver(map)
            subscribeToFilterObserver(map)
        }
    }
    private fun getMap(map: NaverMap) = map.apply {
        createMapOverlays(map)
        setOnMapLoadListener()
        setOnMapDoubleTapListener()
        setOnMapTouchListener()
    }

    private fun createMapOverlays(map: NaverMap) = mapViewModel.run {
        subscribeToStoreMarkersObserver(map)
        subscribeToPolygonOverlaysObserver(map)
        subscribeToCountMarkersObserver(map)
    }

    //!-- create Store Markers
    private fun MapViewModel.subscribeToStoreMarkersObserver(map: NaverMap) {
        storeMarkers.observe(viewLifecycleOwner) {
            it?.let {
                createStoreMarkers(it.first, true, map)
                createStoreMarkers(it.second, false, map)
            }
        }
    }
    private fun createStoreMarkers(markers: List<InfoWindow>, hasInfoWindow: Boolean, map: NaverMap) = lifecycleScope.launch {
        if(!hasInfoWindow) delay(100L)
        for(i in markers.indices) { markers[i].setStoreMarker(hasInfoWindow, map) }
    }
    private suspend fun InfoWindow.setStoreMarker(hasInfoWindow: Boolean, map: NaverMap) {
        setOnMarkerClickListener()
        adapter = withContext(Dispatchers.IO) {
            mapUtils.getAdapter(tag as StoreMapItem, false, hasInfoWindow)
        }
        this.map = map
    }

    private var hasInfoClickedMarker: InfoWindow? = null
    private var hasNotInfoClickedMarker: InfoWindow? = null
    private fun InfoWindow.setOnMarkerClickListener() = setOnClickListener {
        // NaverMap.setOnMapTouchListener is delivered click event by returning false from Overlay. not allow re-settings.
        if(hasInfoClickedMarker == it || hasNotInfoClickedMarker == it) return@setOnClickListener false
        updateClickedMarker(this)
        openStoreBottomSheet(tag as StoreMapItem)
        true
    }
    private fun updateClickedMarker(newClickedMarker: InfoWindow?) {
        hasInfoClickedMarker = hasInfoClickedMarker?.setClickedState(clicked = false, hasInfoWindow = true)
        hasNotInfoClickedMarker = hasNotInfoClickedMarker?.setClickedState(clicked = false, hasInfoWindow = false)

        val markerPair = newClickedMarker?.let { mapViewModel.getStoreMarkerPair(it) }
        hasInfoClickedMarker = markerPair?.get(0)?.setClickedState(clicked = true, hasInfoWindow = true)
        hasNotInfoClickedMarker = markerPair?.get(1)?.setClickedState(clicked = true, hasInfoWindow = true)
    }
    private fun InfoWindow?.setClickedState(clicked: Boolean, hasInfoWindow: Boolean, store: StoreMapItem? = null) = this?.apply {
        val store = store ?: tag as StoreMapItem
        tag = store
        adapter = mapUtils.getAdapter(store, clicked, hasInfoWindow)
        zIndex = if (clicked) MapUtils.Z_INDEX_CLICKED else MapUtils.Z_INDEX_DEFAULT
    }


    //!-- create Polygon Overlays
    private fun MapViewModel.subscribeToPolygonOverlaysObserver(map: NaverMap) {
        polygonOverlays.observe(viewLifecycleOwner) {
            it?.let { createPolygonOverlays(it, map) }
        }
    }
    private fun createPolygonOverlays(polygons: List<PolygonOverlay>, map: NaverMap) = polygons.forEach { polygon ->
        polygon.map = map
        polygon.setOnClickListener { onPolygonClick(polygon, map) }
    }
    private fun onPolygonClick(polygon: PolygonOverlay?, map: NaverMap) = polygon?.run {
        color = Resource.colorMatcheapLightGray
        map.updateCamera(mapUtils.getCenterLatLng(tag as String), MapUtils.MAP_POLYGON_CAMERA_ZOOM)
        registerHandler(delay = 1000L) { color = Resource.colorMatcheapLightYellow }
        true
    } ?: false

    private fun NaverMap.updateCamera(latLng: LatLng, zoom: Double) {
        val cameraUpdate = mapUtils.getCameraUpdate(latLng, zoom)
        moveCamera(cameraUpdate)
    }


    //!-- create Count Markers
    private fun MapViewModel.subscribeToCountMarkersObserver(map: NaverMap) {
        countMarkers.observe(viewLifecycleOwner) {
            it?.let { createCountMarkers(it, map) }
        }
    }
    private fun createCountMarkers(markers: List<Marker>, map: NaverMap) = markers.forEach { marker ->
        marker.map = map
        marker.setOnClickListener { onPolygonClick(mapViewModel.createdPolygon[marker.tag as String], map) }
    }


    private fun NaverMap.setOnMapLoadListener() = addOnLoadListener {
        with(binding) {
            progressView.visibility = View.GONE
            animationView.cancelAnimation()
        }
        addOnZoomListeners()
    }

    private fun NaverMap.addOnZoomListeners() = addOnCameraIdleListener {
        val zoom = cameraPosition.zoom
        binding.zoom = zoom
        if(zoom >= MapUtils.MAP_INFO_MAX_ZOOM) updateAddress(cameraPosition.target)
    }

    private fun updateAddress(latLng: LatLng) {
        locationViewModel.updateAddress(latLng.latitude, latLng.longitude)
    }

    private fun NaverMap.setOnMapDoubleTapListener() = setOnMapDoubleTapListener { _, _ ->
        resetCurrentLocation()
        true
    }
    fun resetCurrentLocation() {
        locationViewModel.getLastLocation()
    }

    private fun NaverMap.setOnMapTouchListener() = setOnMapClickListener { _, _ -> closeStoreBottomSheet() }


    //!--location Overlay, camera
    @Inject lateinit var mapUtils: MapUtils
    private var curLatLng = LatLng(MapUtils.SEOUL_CITY_HALL_LAT, MapUtils.SEOUL_CITY_HALL_LNG)
    private fun subscribeToLocationObserver(map: NaverMap) {
        locationViewModel.location.observe(viewLifecycleOwner) {
            handleLocationUpdate(it, map)
        }
    }
    private fun handleLocationUpdate(location: Location?, map: NaverMap) = location?.run {
        curLatLng = LatLng(latitude, longitude)
        mapUtils.createLocationOverlay(curLatLng, map)
        map.updateCamera(curLatLng, MapUtils.MAP_DEFAULT_ZOOM)
    }


    //!--infoBottomSheet
    private fun openStoreBottomSheet(item: StoreMapItem) {
        initStoreBottomSheet(item)
        binding.mapFragment.startTransition(R.id.open_transition)
    }
    private fun closeStoreBottomSheet() = hasInfoClickedMarker?.let {
        binding.mapFragment.startTransition(R.id.close_transition)
        updateClickedMarker(null)
    }

    private fun initStoreBottomSheet(item: StoreMapItem) = binding.bottomSheet.apply {
        store = item
        view.setOnClickListener { navigateToStoreDetails(item.id) }
    }

    //!-- bookmark
    @Inject lateinit var connectivityManager: ConnectivityManager
    fun updateBookmarkState(store: StoreMapItem, isChecked: Boolean) = lifecycleScope.launch {
        connectivityManager.activeNetwork?.let {
            store.bookmarked = isChecked
            updateClickedMarker(store)
            bookmarkViewModel.updateBookmark(store.id, store.code, isChecked)
        } ?: cancelBookmarkUpdate()
    }
    private fun updateClickedMarker(store: StoreMapItem) {
        hasInfoClickedMarker.setClickedState(clicked = true, hasInfoWindow = true, store)
        hasNotInfoClickedMarker.setClickedState(clicked = true, hasInfoWindow = true, store)
    }
    private fun cancelBookmarkUpdate() = binding.bottomSheet.apply {
        showToastMessage(MESSAGE_NETWORK_ERROR)
        btnBookmark.isChecked = false
        store?.run {
            bookmarked = false
            updateClickedMarker(this)
        }
    }


    //!-- filter
    fun openMapFilter() {
        closeStoreBottomSheet()
        setOnFilterOpen()
        addOnFilterChangeListener()
    }

    private fun setOnFilterOpen() {
        val address = locationViewModel.getCurrentAddress()
        requireActivity().supportFragmentManager.setFragmentResult(FILTER_OPEN, bundleOf(ADDRESS to address))
    }
    @Suppress("DEPRECATION")
    private fun addOnFilterChangeListener() = requireActivity().supportFragmentManager
        .setFragmentResultListener(FILTER_, viewLifecycleOwner) { _, result ->
            val filter = result.getSerializable(FILTER_) as Filter
            mapViewModel.updateFilter(filter)
        }

    private fun subscribeToFilterObserver(map: NaverMap) = mapViewModel.run {
        filter.observe(viewLifecycleOwner) {
            it?.run {
                initCountMarkerData()
                updateRangeCircleOverlay(it.distance, map)
                filterStoreMarkers()
            }
        }
    }

    // To update the number of filtered markers, pre-count need to initialize into zero.
    private fun MapViewModel.initCountMarkerData() = createdCountMarkers.forEach { (_, marker) ->
        marker.captionText = DEFAULT_
    }

    private var createdCircleOverlay : CircleOverlay? = null
    private fun updateRangeCircleOverlay(distance: Double, map: NaverMap) {
        createdCircleOverlay = createdCircleOverlay?.let {
            it.map = null
            null
        }
        createRangeCircleOverlay(distance, map)
    }
    private fun createRangeCircleOverlay(distance: Double, map: NaverMap) {
        if(distance == NOT_DISTANCE) return
        createdCircleOverlay = mapUtils.createCircleOverlay(curLatLng, distance, map)
    }

    private fun Filter.filterStoreMarkers() = lifecycleScope.launch {
        val markers = mapViewModel.createdStoreMarkers.first
        for(i in markers.indices) {
            val store = markers[i].tag as? StoreMapItem ?: return@launch

            val isRegionMatched = region == store.gu || region == ALL_SELECT
            val isCategoryMatched = category.contains(store.code)
            //val isDistanceMatched = distance == NOT_DISTANCE || distance > store.distance
            val isBookmarkMatched = !bookmarked || store.bookmarked

            //val isNotFiltered = isRegionMatched && isCategoryMatched && isDistanceMatched && isBookmarkMatched
            val isNotFiltered = isRegionMatched && isCategoryMatched && isBookmarkMatched
            when {
                isNotFiltered -> mapViewModel.applyToNotFilteredOverlay(markers[i], store.gu)
                else -> mapViewModel.applyToFilteredOverlay(markers[i])
            }
        }
    }

    private fun MapViewModel.applyToNotFilteredOverlay(marker: InfoWindow, gu: String) {
        getStoreMarkerPair(marker)?.forEach { it.isVisible = true }
        createdCountMarkers[gu]?.run { captionText = "${captionText.toInt() + 1}" }
    }

    private fun MapViewModel.applyToFilteredOverlay(marker: InfoWindow) = getStoreMarkerPair(marker)?.forEach {
        it.isVisible = false
    }


    //!-- center camera radius re-search
    fun requestReSearch() {
        // re-setting map data
    }


    override fun subscribeToObservers() {
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
    ) { handleStoreDetailsActivityForResult(it.data) }

    // when back from StoreDetailsActivity, update current clicked store because bookmark state could change.
    @Suppress("DEPRECATION")
    private fun handleStoreDetailsActivityForResult(intent: Intent?) = intent?.run {
        lifecycleScope.launch {
            val store = getSerializableExtra(STORE_) as StoreMapItem
            updateClickedMarker(store)
            initStoreBottomSheet(store)
        }
    }

    override fun setOnBackPressedListener() {
        handleExistDeepLinkNavigation()
    }

    override fun destroyGlobalVariables() {
        mapViewModel.init()
        hasInfoClickedMarker = null
        hasNotInfoClickedMarker = null
        createdCircleOverlay = null
        requireActivity().supportFragmentManager.setFragmentResult(FILTER_OPEN, bundleOf())
    }

}