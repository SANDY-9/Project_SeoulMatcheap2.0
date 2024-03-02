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
import com.sandy.matcheap.common.APP_PREFS_SETTINGS
import com.sandy.matcheap.common.MESSAGE_VERSION_UPDATE
import com.sandy.matcheap.common.TYPE_CATEGORY
import com.sandy.matcheap.common.TYPE_REGION
import com.sandy.seoul_matcheap.MatcheapApplication.Companion.showToastMessage
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.adapters.RegionSpinnerAdapter
import com.sandy.seoul_matcheap.adapters.SurroundingStoreAdapter
import com.sandy.seoul_matcheap.databinding.FragmentHomeBinding
import com.sandy.seoul_matcheap.extensions.updateLocation
import com.sandy.seoul_matcheap.ui.BaseFragment
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
        // currentVersion: 현재 실행중인 최신 앱의 버전, savedVersion: 저장되어 있는 이전 앱 버전
        val currentVersion = BuildConfig.VERSION_NAME
        val savedVersion = AppPrefsUtils.getSavedAppVersion(prefs)

        // 현재 버전과 저장되어 있는 버전이 같으면 새로운 버전으로 업데이트 한게 아니므로 함수 종료
        if(savedVersion == currentVersion) return

        // savedVersion == null이면 앱이 처음 설치된 상태이므로 업데이트 완료 메시지를 띄워서는 안된다.
        if(savedVersion != null) showToastMessage(requireContext(), MESSAGE_VERSION_UPDATE)
        // 최신 버전으로 저장한다.
        saveCurrentVersion(currentVersion)
    }

    private fun saveCurrentVersion(currentVersion: String) {
        AppPrefsUtils.saveLatestAppVersion(prefs, currentVersion)
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
            isLoaded = false
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
    private fun checkSavedBackstackState(DEFAULT_POSITION: Int = 0) = binding.apply {
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
    private fun showRegionSpinnerView(CHANGED_ROTATION: Float = 0f, DEFAULT_POSITION: Int = 0) = binding.apply {
        setRegionSpinnerPositionY()
        regionSpinnerView.visibility = View.VISIBLE
        spinnerIcon.rotation = CHANGED_ROTATION
        rvRegionSpinner.scrollToPosition(DEFAULT_POSITION)
    }

    // region spinner가 화면에서 사라질 때, spinner아이콘과 spinner position은 defalut값으로 돌아가야함
    private fun closeRegionSpinnerView(DEFAULT_ROTATION: Float = 180.0f) = binding.apply {
        regionSpinnerView.visibility = View.GONE
        spinnerIcon.rotation = DEFAULT_ROTATION
        rvRegionSpinner.y = spinnerY
    }

    override fun subscribeToObservers() {
        // Location ViewModel
        locationViewModel.location.observe(viewLifecycleOwner) {
            handleLocationInfo(it)
        }

        // Home ViewModel
        homeViewModel.surroundingStoresState.observe(viewLifecycleOwner) {
            handleSurroundingStoresState(it)
        }
        homeViewModel.randomStoreState.observe(viewLifecycleOwner) {
            handleRandomStoreState(it)
        }
        homeViewModel.forecastState.observe(viewLifecycleOwner) {
            handleForecastState(it)
        }
    }

    private var isLoaded = false
    private fun handleLocationInfo(location: Location?) = location?.let {
        with(homeViewModel) {
            if(!isLoaded) {
                updateRandomStoreList()
                updateForecast(it)
                updateSurroundingStoreList(it)
                isLoaded = true
            }
        }
    }

    private fun handleSurroundingStoresState(state: SurroundingStoresState?) = state?.let {
        surroundingStoreAdapter?.submitList(it.data)
    }

    private fun handleRandomStoreState(state: RandomStoresState?) = state?.let {

    }

    private fun handleForecastState(state: ForecastState?) = state?.let {
        binding.progressView.run {
            if(it.isLoading) {
                root.visibility = View.VISIBLE
                fail.visibility = View.GONE
            } else {
                root.visibility = View.GONE
            }
        }
        if(it.error.isNotBlank()) {
            if(!isLoaded) showToastMessage(requireContext(), state.error)
            binding.progressView.fail.visibility = View.VISIBLE
            isLoaded = true
        }
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
