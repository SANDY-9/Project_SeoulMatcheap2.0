package com.sandy.seoul_matcheap.ui.more.settings

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.SharedPreferences
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.*
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.FragmentSettingsBinding
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.util.*
import com.sandy.seoul_matcheap.util.constants.*
import com.sandy.seoul_matcheap.util.helper.AppPrefsUtils
import com.sandy.seoul_matcheap.util.helper.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
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

    override fun initView() = binding.run {
        switchView.setup()

        btnTimeSetting.setOnTimeSettingButtonClickListener()
        btnApply.setOnTimeApplyButtonClickListener()
        btnBack.setOnBackButtonClickListener()
    }
    private fun View.setup() {
        setIsVisible(PermissionHelper.isGrantedNotificationPermission(requireContext()))
        setOnClickListener()
    }
    private fun View.setIsVisible(isVisible: Boolean) {
        visibility = if(isVisible) View.GONE else View.VISIBLE
    }
    private fun View.setOnClickListener() = setOnClickListener {
        permissionRequest.launch(POST_NOTIFICATIONS)
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
        showToastMessage(MESSAGE_PERMISSION_WARNING_NOTIFICATION)
        startPermissionSettingsIntent()
    }
    override fun handlePermissionSettingsActivityResult() {
        val isGrantedNotificationPermission = PermissionHelper.isGrantedNotificationPermission(requireContext())
        when {
            isGrantedNotificationPermission -> switchToIsGrantedPermissionState()
            else -> showToastMessage(MESSAGE_PERMISSION_WARNING_NOTIFICATION)
        }
    }

    private fun TextView.setOnTimeSettingButtonClickListener() = setOnClickListener {
        openTimePickerView()
    }

    private fun openTimePickerView() {
        binding.pickerView.visibility = View.VISIBLE
    }

    private fun Button.setOnTimeApplyButtonClickListener() = setOnClickListener {
        saveTimeSettings()
        closeTimePickerView()
    }

    private fun saveTimeSettings() {
        binding.timePicker.clearFocus()
        settingsViewModel.saveTime()
        showToastMessage(MESSAGE_NOTIFICATION_SETTINGS)
    }

    @Inject lateinit var inputManager: InputMethodManager
    fun closeTimePickerView() {
        dropDownSoftKeyboard(inputManager)
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
        showToastMessage(if(isChecked) MESSAGE_NOTIFICATION_ALLOW else MESSAGE_NOTIFICATION_DENY)
    }

}
