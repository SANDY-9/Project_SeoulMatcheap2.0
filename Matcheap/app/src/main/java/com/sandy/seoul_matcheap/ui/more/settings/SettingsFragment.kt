package com.sandy.seoul_matcheap.ui.more.settings

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.SharedPreferences
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.*
import com.sandy.seoul_matcheap.MatcheapApplication.Companion.showToastMessage
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.FragmentSettingsBinding
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.util.*
import com.sandy.seoul_matcheap.util.constants.*
import com.sandy.seoul_matcheap.util.helper.AppPrefsUtils
import com.sandy.seoul_matcheap.util.helper.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import dropDownSoftKeyboard
import setIsVisible
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(R.layout.fragment_settings) {

    private val settingsViewModel : SettingsViewModel by viewModels()

    @Inject @Named(APP_PREFS_SETTINGS)
    lateinit var prefs: SharedPreferences

    override fun setupBinding(): FragmentSettingsBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = settingsViewModel
            fragment = this@SettingsFragment
            swichNotifyState.isChecked = AppPrefsUtils.getNotificationState(prefs)
        }
    }

    override fun initView() {
        binding.switchView.apply {
            setIsVisible(PermissionHelper.isGrantedNotificationPermission(requireContext()))
            setOnClickListener {
                permissionRequest.launch(POST_NOTIFICATIONS)
            }
        }
    }

    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        handleNotificationPermissionResult(isGranted)
    }
    private fun handleNotificationPermissionResult(isGrantedNotificationPermission: Boolean) = when {
        isGrantedNotificationPermission -> switchToIsGrantedPermissionState()
        else -> hasNotGrantedNotificationPermission()
    }

    private fun switchToIsGrantedPermissionState()  {
        with(binding) {
            swichNotifyState.isChecked = true
            switchView.setIsVisible(true)
        }
    }

    private fun hasNotGrantedNotificationPermission() {
        showToastMessage(requireContext(), MESSAGE_PERMISSION_WARNING_NOTIFICATION)
        PermissionHelper.startPermissionSettingsIntent(requireContext(), permissionSettingsIntentLauncher)
    }

    //!-- 퍼미션 관련
    private val permissionSettingsIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        handlePermissionSettingsActivityResult()
    }

    private fun handlePermissionSettingsActivityResult() {
        val isGrantedNotificationPermission = PermissionHelper.isGrantedNotificationPermission(requireContext())
        when {
            isGrantedNotificationPermission -> switchToIsGrantedPermissionState()
            else -> showToastMessage(requireContext(), MESSAGE_PERMISSION_WARNING_NOTIFICATION)
        }
    }

    fun openTimePickerView() {
        binding.pickerView.visibility = View.VISIBLE
    }

    fun saveTimeSettings() {
        binding.timePicker.clearFocus()
        settingsViewModel.saveTime()
        showToastMessage(requireContext(), MESSAGE_NOTIFICATION_SETTINGS)
        closeTimePickerView()
    }

    @Inject lateinit var inputManager: InputMethodManager
    fun closeTimePickerView() {
        requireActivity().dropDownSoftKeyboard(inputManager)
        binding.pickerView.visibility = View.GONE
        settingsViewModel.initTime()
    }

    private var notOperation = true
    fun changedStateNotification(isChecked: Boolean) {
        // onCheckChange should not work at first
        val operation = isChecked && notOperation && AppPrefsUtils.getNotificationState(prefs)
        if (operation) {
            notOperation = false
            return
        }
        applyIsChecked(isChecked)
    }
    private fun applyIsChecked(isChecked: Boolean) {
        if(!isChecked) closeTimePickerView()
        settingsViewModel.setNotificationState(isChecked)
        showToastMessage(
            requireContext(),
            if(isChecked) MESSAGE_NOTIFICATION_ALLOW else MESSAGE_NOTIFICATION_DENY
        )
    }

}
