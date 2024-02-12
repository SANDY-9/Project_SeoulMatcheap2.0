package com.sandy.seoul_matcheap.ui.splash

import android.Manifest.permission.*
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
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
import com.sandy.seoul_matcheap.MatcheapApplication.Companion.showToastMessage
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.StoreDatabase
import com.sandy.seoul_matcheap.databinding.FragmentSplashBinding
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.BaseFragment
import com.sandy.seoul_matcheap.notification.NotificationScheduler
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
    private fun fetchRemoteConfig() = when {
        AppPrefsUtils.getSavedAppVersion(prefs) != null -> receiveRemoteConfig()
        else -> requestPermissions()
    }

    private fun receiveRemoteConfig() {
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
    }

    private fun FirebaseRemoteConfig.addOnFetchCompleteListener(remoteConfig: FirebaseRemoteConfig) {
        fetchAndActivate().addOnCompleteListener(requireActivity()) {
            val newVersion = remoteConfig.getString(APP_VERSION)
            checkAppVersion(checkable = it.isSuccessful, versionCheck = BuildConfig.VERSION_NAME == newVersion)
        }
    }

    private fun checkAppVersion(checkable: Boolean, versionCheck: Boolean) = when {
        checkable && !versionCheck -> showAppUpdateNoticeDialog()
        else -> requestPermissions().also {
            if(!checkable) showToastMessage(requireContext(), MESSAGE_NETWORK_ERROR)
        }
    }

    private fun showAppUpdateNoticeDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(APP_NAME)
            .setMessage(MESSAGE_NEW_VERSION_UPDATE)
            .setCancelable(false)
            .setNegativeButton(BUTTON_TITLE_UPDATE) { _, _ ->
                navigateToBrowser(MARKET_URI + requireContext().packageName)
                setOnBackPressedListener()
            }
            .setPositiveButton(BUTTON_TITLE_CONFIRM) { _, _ -> requestPermissions() }
            .show()
    }

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

    private var register = false
    private fun hasNotificationPermission() {
        val savedNotificationState = AppPrefsUtils.getNotificationState(prefs)
        val notificationPermissionState = AppPrefsUtils.getNotificationPermissionState(prefs)
        if(notificationPermissionState && !savedNotificationState) {
            register = false
            return
        }

        AppPrefsUtils.setNotificationPermissionState(prefs, true)
        register = true
    }
    private fun hasNotNotificationPermission() {
        AppPrefsUtils.setNotificationPermissionState(prefs, false)
        register = false
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
    private val locationViewModel: LocationViewModel by activityViewModels()
    private fun requestLocationUpdate() {
        val isLocationUpdateEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!isLocationUpdateEnabled) showToastMessage(requireContext(), MESSAGE_GPS_WARNING)
        locationViewModel.updateLocation()
        binding.tvLoading.visibility = View.VISIBLE
    }

    private fun hasNotLocationPermission() {
        showToastMessage(requireContext(), MESSAGE_PERMISSION_WARNING_LOCATION)
        PermissionHelper.startPermissionSettingsIntent(requireContext(), permissionSettingsIntentLauncher)
    }

    private val permissionSettingsIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        handlePermissionSettingsActivityResult()
    }

    private fun handlePermissionSettingsActivityResult() {
        val isGrantedLocationPermission = PermissionHelper.isGrantedLocationPermission(requireContext())
        when {
            isGrantedLocationPermission -> requestLocationUpdate()
            else -> showToastMessage(requireContext(), MESSAGE_PERMISSION_WARNING_LOCATION)
        }
    }

    override fun initView() { /* NO_OP */ }

    override fun subscribeToObservers() {
        locationViewModel.location.observe(viewLifecycleOwner) {
            handleLocationAndCheckDB(it)
        }
        loadViewModel.dataLoadSate.observe(viewLifecycleOwner) {
            handleLoadState(it)
        }
    }

    private fun handleLocationAndCheckDB(location: Location?) = location?.let {
        locationViewModel.stopLocation()
        checkDatabaseVersion()
    }

    // upgrade database using location information
    @Inject lateinit var db: StoreDatabase
    private val loadViewModel: LoadViewModel by viewModels()
    private fun checkDatabaseVersion() {
        val latestDatabaseVersion = db.openHelper.readableDatabase.version.toString()
        val savedDatabaseVersion = AppPrefsUtils.getSavedDatabaseVersion(prefs)
        when(latestDatabaseVersion) {
            savedDatabaseVersion -> loadViewModel.setLoadState(true)
            else -> fetchDatabase(requireContext())
        }
    }

    private fun fetchDatabase(context: Context) {
        val storeData = DataHelper.downloadStoreData(context)
        val polygonData = DataHelper.downloadPolygonData(context)
        loadViewModel.updateDatabase(storeData, polygonData)
    }


    private fun handleLoadState(isLoadEnd: Boolean) {
        if(isLoadEnd) {
            saveDatabaseVersion()
            setNotificationSchedule()
            navigateToDestination()
        }
    }

    @Inject lateinit var notificationScheduler: NotificationScheduler
    private fun setNotificationSchedule() {
        val time = AppPrefsUtils.getSavedTime(prefs)
        AppPrefsUtils.setNotificationState(prefs, register)
        notificationScheduler.setNotificationSchedule(register, time)
    }

    private fun saveDatabaseVersion() {
        val latestDatabaseVersion = db.openHelper.readableDatabase.version.toString()
        AppPrefsUtils.saveLatestDatabaseVersion(prefs, latestDatabaseVersion)
    }

    private fun navigateToDestination() {
        val uri = requireActivity().intent.data
        val option = NavOptions.Builder().setPopUpTo(R.id.nav_graph, false).build()
        findNavController().navigate(getDestination(uri?.path), null, option)
    }

    private fun getDestination(path: String?) = when(path) {
        SEARCH_ -> R.id.action_splashFragment_to_searchFragment
        MAP_ -> R.id.action_splashFragment_to_mapFragment
        BOOKMARK_ -> R.id.action_splashFragment_to_bookMarkFragment
        else -> R.id.action_splashFragment_to_homeFragment
    }

    override fun setOnBackPressedListener() {
        requireActivity().finishAndRemoveTask()
    }

    companion object {
        private const val BUTTON_TITLE_UPDATE = "업데이트"
        private const val BUTTON_TITLE_CONFIRM = "확인"
        private const val MARKET_URI = "market://details?id="
    }

}