package com.sandy.seoul_matcheap.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.ActivityMainBinding
import com.sandy.seoul_matcheap.ui.map.MapFilterFragment
import com.sandy.seoul_matcheap.util.constants.DEFAULT_
import com.sandy.seoul_matcheap.util.constants.FILTER_
import com.sandy.seoul_matcheap.util.constants.FILTER_OPEN
import com.sandy.seoul_matcheap.util.constants.HOME_
import com.sandy.seoul_matcheap.util.constants.MAP_
import com.sandy.seoul_matcheap.util.constants.MORE_
import com.sandy.seoul_matcheap.util.constants.SEARCH_
import com.sandy.seoul_matcheap.util.constants.SPLASH_
import com.sandy.seoul_matcheap.util.custom.SoftKeyboardDetectorView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initView()
        supportFragmentManager.addOnFilterOpenListener()
    }

    private fun initView() = binding.apply {
        with(bottomNavigationView) {
            setupWithNavController(getNavController())
            setOnItemReselectedListener { /* NO-OP */ }
        }
        keyboardDetectorView.addOnKeyboardDropDownListener()
    }

    private fun getNavController() = findNavController(R.id.navHostFragment).apply {
        graph.setStartDestination(R.id.homeFragment)
        addOnDestinationChangedListener { _, destination, _ ->
            val destination = destination.label.toString()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                setWindowSizeForNewAndroidVersion(destination)
            } else {
                setWindowSizeForOldAndroidVersion(destination)
            }
            controlBottomNavVisibility(destination)
        }
    }

    @Suppress("DEPRECATION")
    private fun setWindowSizeForOldAndroidVersion(label: String) = window?.apply {
        statusBarColor = when (label) {
            SPLASH_, HOME_ -> resources.getColor(R.color.matcheap_blue)
            else -> Color.TRANSPARENT
        }
        decorView.systemUiVisibility = when (label) {
            MAP_ -> SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            SPLASH_, HOME_ -> SYSTEM_UI_FLAG_LAYOUT_STABLE
            else -> SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    // 안드로이드 15(VERSION_CODES.VANILLA_ICE_CREAM) 부터
    private fun setWindowSizeForNewAndroidVersion(label: String) {
        setLightStatusBars(label)
        setLayoutFullscreen(label)
        setStatusBarColor(label)
    }

    private fun setLightStatusBars(label: String) {
        WindowCompat.getInsetsController(window, window.decorView).run {
            isAppearanceLightStatusBars = when (label) {
                MAP_ -> true
                SPLASH_, HOME_ -> false
                else -> true // 기타 경우
            }
        }
    }

    private fun setLayoutFullscreen(label: String) {
        when (label) {
            SPLASH_, MAP_ -> setFullscreenMode()
            else -> setNormalMode()
        }
    }

    private fun setFullscreenMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }
    }

    private fun setNormalMode() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, 0)
            insets
        }
    }

    private fun setStatusBarColor(label: String) {
        val color = when (label) {
            SPLASH_, HOME_ -> resources.getColor(R.color.matcheap_blue, null)
            else -> Color.TRANSPARENT
        }
        binding.root.setBackgroundColor(color)
    }

    private fun controlBottomNavVisibility(destination: String) {
        val visibleDestination = arrayOf(HOME_, MAP_, SEARCH_, MORE_)
        binding.bottomNavigationView.visibility =
            if (visibleDestination.contains(destination)) View.VISIBLE else View.GONE
    }

    private fun SoftKeyboardDetectorView.addOnKeyboardDropDownListener() {
        setOnShownKeyboard {
            controlBottomNavVisibility(DEFAULT_)
        }
        setOnHiddenKeyboard {
            controlBottomNavVisibility(SEARCH_)
            currentFocus?.clearFocus()
        }
    }

    //!-- 맵 필터 동작 관련
    private fun FragmentManager.addOnFilterOpenListener() {
        var fragment: MapFilterFragment? = null
        setFragmentResultListener(FILTER_OPEN, this@MainActivity) { _, result ->
            val open = result.getBoolean(FILTER_OPEN)
            fragment = when {
                open -> addFilterFragment(fragment ?: MapFilterFragment(), fragment == null)
                else -> removeFilter(fragment)
            }
        }
    }

    private fun addFilterFragment(fragment: MapFilterFragment, create: Boolean) = fragment.also {
        when {
            create -> supportFragmentManager.commit {
                add(R.id.fragmentContainerView, it, FILTER_)
                setReorderingAllowed(true)
            }

            else -> it.openTransition()
        }
    }

    private fun removeFilter(fragment: MapFilterFragment?): MapFilterFragment? {
        fragment?.let { supportFragmentManager.commit { remove(fragment) } }
        return null
    }

}