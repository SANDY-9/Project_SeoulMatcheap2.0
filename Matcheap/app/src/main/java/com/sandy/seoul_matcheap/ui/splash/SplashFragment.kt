package com.sandy.seoul_matcheap.ui.splash

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.sandy.seoul_matcheap.BuildConfig
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.FragmentSplashBinding
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.ui.more.settings.NotificationWorker
import com.sandy.seoul_matcheap.ui.more.settings.Time
import com.sandy.seoul_matcheap.util.*
import com.sandy.seoul_matcheap.util.constants.*
import com.sandy.seoul_matcheap.util.helper.AppPrefsUtils
import com.sandy.seoul_matcheap.util.helper.DataHelper
import com.sandy.seoul_matcheap.util.helper.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    override fun setupBinding() = binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchRemoteConfig()
    }
    @Inject @Named(APP_PREFS_SETTINGS)
    lateinit var prefs : SharedPreferences
    private fun fetchRemoteConfig() = AppPrefsUtils.getSavedVersion(prefs)?.let { _ ->
        val configSettings = remoteConfigSettings { }
        Firebase.remoteConfig.run {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
            addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) = Unit
                override fun onError(error: FirebaseRemoteConfigException) = Unit
            })
            addOnFetchCompleteListener(this)
        }
    } ?: requestPermissions()

    private fun FirebaseRemoteConfig.addOnFetchCompleteListener(remoteConfig: FirebaseRemoteConfig) {
        fetchAndActivate().addOnCompleteListener(requireActivity()) {
            checkVersion(it.isSuccessful, remoteConfig)
        }
    }

    private fun checkVersion(isCheckable: Boolean, remoteConfig: FirebaseRemoteConfig) = when {
        isCheckable -> showAppUpdateNoticeDialog(BuildConfig.VERSION_NAME != remoteConfig.getString(APP_VERSION))
        else -> showToastMessage(MESSAGE_NETWORK_ERROR).also { requestPermissions() }
    }
    private fun showAppUpdateNoticeDialog(show: Boolean) = when {
        show -> createAppUpdateNoticeDialogBuilder().show()
        else -> requestPermissions()
    }
    private fun createAppUpdateNoticeDialogBuilder() = MaterialAlertDialogBuilder(requireContext())
        .setTitle(APP_NAME)
        .setMessage(MESSAGE_NEW_VERSION_UPDATE)
        .setCancelable(false)
        .setNegativeButton(BUTTON_TITLE_UPDATE) { _, _ ->
            navigateToBrowser(MARKET_URI + requireContext().packageName)
            setOnBackPressedListener()
        }
        .setPositiveButton(BUTTON_TITLE_CONFIRM) { _, _ -> requestPermissions() }

    private fun requestPermissions() {
        val permissions = arrayOf(
            POST_NOTIFICATIONS,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )
        permissionRequestLauncher.launch(permissions)
    }

    private val permissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        //notification permission
        handleNotificationPermission(it[POST_NOTIFICATIONS]?:false)

        //location permission
        handleAllLocationPermission(
            it[ACCESS_FINE_LOCATION] ?: false,
            it[ACCESS_COARSE_LOCATION] ?: false
        )
    }
    private fun handleNotificationPermission(isGranted: Boolean) = when {
        isGranted -> hasNotificationPermission()
        else -> hasNotNotificationPermission()
    }

    private fun hasNotificationPermission() {
        val savedNotificationState = AppPrefsUtils.getNotificationState(prefs)
        val notificationPermissionState = AppPrefsUtils.getNotificationPermissionState(prefs)
        if(notificationPermissionState && !savedNotificationState) {
            setNotificationSchedule(false)
            return
        }

        val time = AppPrefsUtils.getSavedTime(prefs)
        AppPrefsUtils.setNotificationPermissionState(prefs, true)
        setNotificationSchedule(true, time)
    }
    private fun hasNotNotificationPermission() {
        AppPrefsUtils.setNotificationPermissionState(prefs, false)
        setNotificationSchedule(false)
    }
    private fun setNotificationSchedule(isActive: Boolean, time: Time= DEFAULT_HOUR to DEFAULT_MINUTE) {
        AppPrefsUtils.setNotificationState(prefs, isActive)
        NotificationWorker.setNotificationSchedule(requireContext(), isActive, time)
    }

    private fun handleAllLocationPermission(vararg locationPermissions: Boolean) {
        // whether all location permission is granted or not
        val isAllGrantedPermission = !locationPermissions.contains(false)
        when {
            isAllGrantedPermission -> requestLocationUpdate()
            else -> hasNotLocationPermission()
        }
    }

    @Inject lateinit var locationManager : LocationManager
    private val locationViewModel: LocationViewModel by viewModels()
    private fun requestLocationUpdate() {
        binding.tvLoading.visibility = View.VISIBLE
        getGpsProviderState(locationManager)
        locationViewModel.updateLocation()
    }

    private fun hasNotLocationPermission() {
        showToastMessage(MESSAGE_PERMISSION_WARNING_LOCATION)
        startPermissionSettingsIntent()
    }
    override fun handlePermissionSettingsActivityResult() {
        val isGrantedLocationPermission = PermissionHelper.isGrantedLocationPermission(requireContext())
        when {
            isGrantedLocationPermission -> requestLocationUpdate()
            else -> showToastMessage(MESSAGE_PERMISSION_WARNING_LOCATION)
        }
    }

    override fun initView() { /* NO_OP */ }

    override fun subscribeToObservers() {
        subscribeToLocationObserver()
        subscribeToLoadViewModelObserver()
    }

    private fun subscribeToLocationObserver() {
        locationViewModel.location.observe(viewLifecycleOwner) {
            it?.let {
                locationViewModel.stopLocation()
                fetchDatabase(it)
            }
        }
    }

    // upgrade database using location information
    private val loadViewModel: LoadViewModel by viewModels()
    private fun fetchDatabase(location: Location) {
        val dataSaveState = AppPrefsUtils.getDatabaseState(prefs) == APP_DATABASE_STATE

        val storeData = DataHelper.downloadStoreData(requireContext())
        val polygonData = if(dataSaveState) null else DataHelper.downloadPolygonData(requireContext())
        loadViewModel.updateDatabase(location, storeData, polygonData)
    }

    private fun subscribeToLoadViewModelObserver() {
        loadViewModel.dataLoadSate.observe(viewLifecycleOwner) {
            if(it) navigateToHome()
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(getDestination(HOME_), null, getPopUpNavOptions())
    }

    override fun setOnBackPressedListener() {
        requireActivity().finishAffinity()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val intent = (context as Activity).intent
        handleDeepLink(intent.data)
    }
    private fun handleDeepLink(data: Uri?) = data?.path?.let {
        findNavController().navigate(getDestination(it), null, getPopUpNavOptions())
    }
    private fun getDestination(path: String?) = when(path) {
        SEARCH_ -> R.id.action_splashFragment_to_searchFragment
        MAP_ -> R.id.action_splashFragment_to_mapFragment
        BOOKMARK_ -> R.id.action_splashFragment_to_bookMarkFragment
        else -> R.id.action_splashFragment_to_homeFragment
    }

    companion object {
        private const val BUTTON_TITLE_UPDATE = "업데이트"
        private const val BUTTON_TITLE_CONFIRM = "확인"
        private const val MARKET_URI = "market://details?id="
    }

}