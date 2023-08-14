package com.sandy.seoul_matcheap.ui.home

import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.sandy.seoul_matcheap.BuildConfig
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.StoreItem
import com.sandy.seoul_matcheap.databinding.FragmentHomeBinding
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.LocationViewModel.Companion.MESSAGE_INIT_ADDRESS
import com.sandy.seoul_matcheap.util.*
import com.sandy.seoul_matcheap.util.constants.*
import com.sandy.seoul_matcheap.util.helper.AppPrefsUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val locationViewModel: LocationViewModel by activityViewModels()
    private val homeViewModel : HomeViewModel by viewModels()

    override fun setupBinding(): FragmentHomeBinding = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        fragment = this@HomeFragment
        location = locationViewModel
        viewModel = homeViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkVersionUpdate()
        super.onViewCreated(view, savedInstanceState)
    }

    @Inject @Named(APP_PREFS_SETTINGS)
    lateinit var prefs: SharedPreferences
    private fun checkVersionUpdate() {
        val currentVersion = BuildConfig.VERSION_NAME
        when(AppPrefsUtils.getSavedVersion(prefs)) {
            currentVersion -> return
            null -> AppPrefsUtils.saveCurrentVersion(prefs, currentVersion)
            else -> {
                showToastMessage(MESSAGE_VERSION_UPDATE)
                AppPrefsUtils.saveCurrentVersion(prefs, currentVersion)
            }
        }
    }

    private var regionSpinnerAdapter : RegionSpinnerAdapter? = null
    private var surroundingStoreAdapter: SurroundingStoreAdapter? = null
    override fun initGlobalVariables() {
        regionSpinnerAdapter = RegionSpinnerAdapter().apply {
            addOnItemClickListener()
        }
        surroundingStoreAdapter = SurroundingStoreAdapter().apply {
            addOnItemClickListener()
        }
    }

    override fun RecyclerView.Adapter<out RecyclerView.ViewHolder>.addOnItemClickListener() {
        when(this) {
            is RegionSpinnerAdapter -> setOnItemClickListener{
                navigateToStoreList(it, TYPE_REGION)
            }
            is SurroundingStoreAdapter -> setOnItemClickListener {
                navigateToStoreDetails(it.id)
            }
        }
    }

    override fun initView() = binding.run {
        checkSavedBackstackState()

        rvRegionSpinner.addAdapter(regionSpinnerAdapter)
        regionSpinnerView.setOnRegionSpinnerViewClickListener()
        btnRegionSelect.setOnRegionButtonClickListener()

        rvSurroundingStores.addAdapter(surroundingStoreAdapter)

        btnGps.setOnGpsUpdateButtonClickListener()
        btnRefresh.setOnRefreshButtonClickListener()
        progressView.retry.setOnRetryButtonClickListener()
    }

    private var onBackstackCallback = false
    private fun checkSavedBackstackState() = binding.apply {
        if(!onBackstackCallback) {
            nestedScrollView.post {
                nestedScrollView.scrollY = DEFAULT_POSITION
            }
            rvSurroundingStores.scrollToPosition(DEFAULT_POSITION)
        }
        onBackstackCallback = false
    }

    // spinner의 바깥 뷰를 클릭했을 때 region spinner는 화면에서 사라짐
    private fun FrameLayout.setOnRegionSpinnerViewClickListener() = setOnClickListener {
        closeRegionSpinnerView()
    }
    // region spinner가 화면에서 사라질 때, spinner아이콘과 spinner position은 defalut값으로 돌아가야함
    private fun closeRegionSpinnerView() = binding.apply {
        regionSpinnerView.visibility = View.GONE
        spinnerIcon.rotation = DEFAULT_ROTATION
        rvRegionSpinner.y = SPINNER_DEFAULT_POSION_Y
    }

    // RegionButton을 클릭 했을 때, region spinner가 나타나야하며 이 때 region spinner는 반드시 위치가 설정된 후 화면에 보여져야함
    private fun View.setOnRegionButtonClickListener() = setOnClickListener {
        setRegionSpinnerPositionY()
        showRegionSpinnerView()
    }

    private var SPINNER_DEFAULT_POSION_Y: Float = 0.0f
    private fun setRegionSpinnerPositionY() = binding.rvRegionSpinner.apply {
        // spinner의 default positionY를 측정하여 저장
        SPINNER_DEFAULT_POSION_Y = y
        y = SPINNER_DEFAULT_POSION_Y - binding.nestedScrollView.scrollY
    }

    // region spinner가 visible되면 spinner icon이 180도 회전하고 spinner는 defalut postion으로 스크롤 되게 동작하는 설정
    private fun showRegionSpinnerView() = binding.apply {
        regionSpinnerView.visibility = View.VISIBLE
        spinnerIcon.rotation = CHANGED_ROTATION
        rvRegionSpinner.scrollToPosition(DEFAULT_POSITION)
    }

    @Inject lateinit var locationManager: LocationManager
    private var gpsButtonClick = false
    private fun ImageView.setOnGpsUpdateButtonClickListener() = setOnClickListener {
        gpsButtonClick = true
        homeViewModel.setLoadingState(ConnectState.ING)
        updateLocation(locationViewModel, locationManager)
    }

    private fun ImageView.setOnRefreshButtonClickListener() = setOnClickListener {
        homeViewModel.updateRandomStoreList()
    }

    override fun TextView.setOnRetryButtonClickListener() = setOnClickListener {
        homeViewModel.setLoadingState(ConnectState.ING)
        updateLocation(locationViewModel, locationManager)
    }

    override fun subscribeToObservers() {
        subscribeToLoadStateObserver()
        subscribeToLocationObserver()
        subscribeToSurroundingStoresObserver()
    }

    private fun subscribeToLoadStateObserver() {
        homeViewModel.loadingState.observe(viewLifecycleOwner) {
            handleLoadingState(it)
        }
    }

    private fun handleLoadingState(state: ConnectState) = when(state) {
        ConnectState.SUCCESS -> handleLoadSuccess()
        ConnectState.FAIL -> handleLoadFail()
        else -> handleNotLoad()
    }
    private fun handleLoadSuccess() {
        binding.progressView.root.visibility = View.GONE
    }
    private fun handleLoadFail() {
        showNetworkErrorToastMessage()
        binding.progressView.fail.visibility = View.VISIBLE
    }

    private var isLoaded = false
    private fun showNetworkErrorToastMessage() {
        showToastMessage(MESSAGE_NETWORK_ERROR)
    }

    private fun handleNotLoad() {
        with(binding.progressView) {
            root.visibility = View.VISIBLE
            fail.visibility = View.GONE
        }
    }

    private fun subscribeToLocationObserver() = locationViewModel.run {
        location.observe(viewLifecycleOwner) {
            handleLocationInfo(it)
        }
        address.observe(viewLifecycleOwner) {
            handleAddress(it)
        }
    }

    private fun handleLocationInfo(location: Location?) = location?.let {
        with(homeViewModel) {
            updateRandomStoreList()
            updateForecast(it)
            updateSurroundingStoreList(it)
        }
    }

    private fun subscribeToSurroundingStoresObserver() {
        homeViewModel.surroundingStores.observe(viewLifecycleOwner) {
            handleSurroundStores(it)
        }
    }

    private fun handleSurroundStores(stores: List<StoreItem>) {
        surroundingStoreAdapter?.submitList(stores)
    }

    // 현재 주소를 알려주는 toast message는 반드시 gps update button가 호출 되었을 때만 생성 되어야 함
    private fun handleAddress(address: String) {
        if(address == MESSAGE_INIT_ADDRESS || !gpsButtonClick) return
        showToastMessage(address)
        gpsButtonClick = false
    }

    fun navigateToStoreListForCategory(v: View, code: String) {
        v.changeScale()
        navigateToStoreList(code, TYPE_CATEGORY)
    }

    private fun navigateToStoreList(category: String, type: Int) {
        val action = HomeFragmentDirections.actionHomeFragmentToStoreListFragment(
            category = category,
            address = locationViewModel.getCurrentAddress(),
            type = type
        )
        navigateDirections(action)
        onBackstackCallback = true
    }

    override fun setOnBackPressedListener() {
        requireActivity().finishAndRemoveTask()
    }

    override fun destroyGlobalVariables() {
        regionSpinnerAdapter = null
        surroundingStoreAdapter = null
        isLoaded = false
    }

}
