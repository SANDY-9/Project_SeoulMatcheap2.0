package com.sandy.seoul_matcheap.ui.home

import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.sandy.seoul_matcheap.BuildConfig
import androidx.fragment.app.viewModels
import changeScale
import com.sandy.seoul_matcheap.MatcheapApplication.Companion.showToastMessage
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.StoreItem
import com.sandy.seoul_matcheap.databinding.FragmentHomeBinding
import com.sandy.seoul_matcheap.extension.updateLocation
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.ui.LocationViewModel
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

    @Inject lateinit var locationManager: LocationManager

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
        when(val savedVersion = AppPrefsUtils.getSavedAppVersion(prefs)) {
            currentVersion -> return
            else -> AppPrefsUtils.saveLatestAppVersion(prefs, currentVersion).also {
                if(savedVersion != null) showToastMessage(requireContext(), MESSAGE_VERSION_UPDATE)
            }
        }
    }

    private var regionSpinnerAdapter : RegionSpinnerAdapter? = null
    private var surroundingStoreAdapter: SurroundingStoreAdapter? = null
    override fun initGlobalVariables() {
        regionSpinnerAdapter = RegionSpinnerAdapter().apply {
            setOnItemClickListener{
                navigateToStoreList(it, TYPE_REGION)
            }
        }
        surroundingStoreAdapter = SurroundingStoreAdapter().apply {
            setOnItemClickListener {
                navigateToStoreDetails(it.id)
            }
        }
    }

    override fun initView() = binding.run {
        checkSavedBackstackState()

        rvRegionSpinner.adapter = regionSpinnerAdapter
        rvSurroundingStores.adapter = surroundingStoreAdapter

        btnRegionSelect.setOnClickListener {
            //RegionButton을 클릭 했을 때, region spinner가 나타나야하며 이 때 region spinner는 반드시 위치가 설정된 후 화면에 보여져야함
            showRegionSpinnerView()
        }

        regionSpinnerView.setOnClickListener {
            // spinner의 바깥 뷰를 클릭했을 때 region spinner는 화면에서 사라짐
            closeRegionSpinnerView()
        }

        btnGps.setOnClickListener {
            locationManager.updateLocation(locationViewModel, requireContext())
        }

        btnRefresh.setOnClickListener {
            homeViewModel.updateRandomStoreList()
        }

        progressView.retry.setOnClickListener {
            isLoaded = false
            homeViewModel.updateForecast(locationViewModel.getCurLocation())
        }

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


    private var spinnerY: Float = 0.0f
    private fun setRegionSpinnerPositionY() = binding.apply {
        // spinner의 default positionY를 측정하여 저장
        spinnerY = rvRegionSpinner.y
        rvRegionSpinner.y = spinnerY - nestedScrollView.scrollY
    }

    // region spinner가 visible되면 spinner icon이 180도 회전하고 spinner는 defalut postion으로 스크롤 되게 동작하는 설정
    private fun showRegionSpinnerView() = binding.apply {
        setRegionSpinnerPositionY()
        regionSpinnerView.visibility = View.VISIBLE
        spinnerIcon.rotation = CHANGED_ROTATION
        rvRegionSpinner.scrollToPosition(DEFAULT_POSITION)
    }

    // region spinner가 화면에서 사라질 때, spinner아이콘과 spinner position은 defalut값으로 돌아가야함
    private fun closeRegionSpinnerView() = binding.apply {
        regionSpinnerView.visibility = View.GONE
        spinnerIcon.rotation = DEFAULT_ROTATION
        rvRegionSpinner.y = spinnerY
    }

    override fun subscribeToObservers() {
        locationViewModel.location.observe(viewLifecycleOwner) {
            handleLocationInfo(it)
        }
        homeViewModel.loadingState.observe(viewLifecycleOwner) {
            handleLoadingState(it)
        }
        homeViewModel.surroundingStores.observe(viewLifecycleOwner) {
            handleSurroundStores(it)
        }
    }

    private fun handleLocationInfo(location: Location?) = location?.let {
        with(homeViewModel) {
            updateRandomStoreList()
            updateForecast(it)
            updateSurroundingStoreList(it)
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

    private var isLoaded = false
    private fun handleLoadFail() {
        if(!isLoaded) showToastMessage(requireContext(), MESSAGE_NETWORK_ERROR)
        binding.progressView.fail.visibility = View.VISIBLE
        isLoaded = true
    }

    private fun handleNotLoad() {
        with(binding.progressView) {
            root.visibility = View.VISIBLE
            fail.visibility = View.GONE
        }
    }

    private fun handleSurroundStores(stores: List<StoreItem>) {
        surroundingStoreAdapter?.submitList(stores)
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
    }

}
