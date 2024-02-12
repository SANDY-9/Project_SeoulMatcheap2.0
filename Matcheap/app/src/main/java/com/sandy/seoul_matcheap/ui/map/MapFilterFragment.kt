package com.sandy.seoul_matcheap.ui.map

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.DistanceRadius
import com.sandy.seoul_matcheap.databinding.FragmentMapFilterBinding
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.common.*
import com.sandy.seoul_matcheap.ui.home.RegionSpinnerAdapter
import com.sandy.seoul_matcheap.util.constants.*
import com.warkiz.widget.*
import setChangeIconTintColorOnTouch
import setChangeTextColorOnTouch

class MapFilterFragment : BaseFragment<FragmentMapFilterBinding>(R.layout.fragment_map_filter) {

    private val mapViewModel: MapViewModel by activityViewModels()

    override fun setupBinding(): FragmentMapFilterBinding {
        setCategoryAllCheckState(false)
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@MapFilterFragment
        }
    }

    private fun setCategoryAllCheckState(isChecked: Boolean) = binding.run {
        checkedHansik = isChecked
        checkedChina = isChecked
        checkedJapan = isChecked
        checkedOther = isChecked
        checkedWash = isChecked
        checkedBeauty = isChecked
        checkedHotel = isChecked
        checkedStore = isChecked
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        openTransition()
        super.onViewCreated(view, savedInstanceState)
    }

    fun openTransition() = binding.mapFilterLayout.startTransition(R.id.open_transition)

    // to not overlap animation, close transition should start after pre-transition finish.
    private var isPreTransitionEnd = true
    fun closeTransition() {
        if(isPreTransitionEnd) {
            binding.mapFilterLayout.startTransition(R.id.close_transition)
        }
    }

    override fun initView() = binding.run {
        mapFilterLayout.setTransitionListener()
        rvSpinner.addAdapter()
        distanceSeekbar.setOnSeekChangeListener()
        btnDistanceReset.setOnDistanceResetButtonTouchListener()
        btnReset.setOnResetButtonTouchListener()
    }

    private fun MotionLayout.setTransitionListener() = addTransitionListener(
        object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(layout: MotionLayout?, start: Int, end: Int) {
                isPreTransitionEnd = false
            }
            override fun onTransitionChange(layout: MotionLayout?, start: Int, end: Int, progress: Float) { /* NO-OP */ }
            override fun onTransitionCompleted(layout: MotionLayout?, current: Int) {
                isPreTransitionEnd = true
            }
            override fun onTransitionTrigger(layout: MotionLayout?, trigger: Int, positive: Boolean, progress: Float) { /* NO-OP */ }
        }
    )

    private fun RecyclerView.addAdapter() {
        adapter = RegionSpinnerAdapter(TYPE_REGION).apply {
            setOnItemClickListener {
                updateRegionFilter(it)
            }
        }
    }

    private fun updateRegionFilter(param: String) {
        closeRegionSpinnerView()
        binding.tvRegion.text = param
        mapViewModel.filterGu(param)
    }

    fun showRegionSpinnerView() = binding.apply {
        regionSpinnerView.visibility = View.VISIBLE
        spinnerIcon.scaleY = FLIP_180
        rvSpinner.scrollToPosition(DEFAULT_POSITION)
    }

    fun closeRegionSpinnerView() = binding.apply {
        regionSpinnerView.visibility = View.GONE
        spinnerIcon.scaleY = FLIP_0
    }


    // !-- form here, functions about category filter
    fun onAllSelectChecked(isChecked: Boolean) {
        setCategoryAllCheckState(isChecked)
        mapViewModel.filterCode(isChecked)
    }

    fun updateCategoryFilter(isChecked: Boolean, code: String) {
        onCheckedChange(isChecked, code)
        mapViewModel.filterCode(!isChecked, code)
    }

    private fun onCheckedChange(isChecked: Boolean, code: String) = binding.run {
        when (code) {
            HANSIK_ -> checkedHansik = !isChecked
            CHINA_ -> checkedChina = !isChecked
            JAPAN_ -> checkedJapan = !isChecked
            OTHER_ -> checkedOther = !isChecked
            WASH_ -> checkedWash = !isChecked
            BEAUTY_ -> checkedBeauty = !isChecked
            HOTEL_ -> checkedHotel = !isChecked
            STORE_ -> checkedStore = !isChecked
        }
    }


    // !-- form here, functions about distance filter
    private val locationViewModel: LocationViewModel by activityViewModels()
    private fun IndicatorSeekBar.setOnSeekChangeListener() {
        val tickTextColor = resources.getColorStateList(R.color.tick_texts_color, null)
        onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) { /*NO-OP*/ }
            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                binding.distanceSeekbar.tickTextsColorStateList(tickTextColor)
            }
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                seekBar?.let { updateDistanceFilter(it) }
            }
        }
    }

    // !-- strings.xml -> tick_texts_array
    private fun updateDistanceFilter(seekBar: IndicatorSeekBar) {
        val param = when(seekBar.progress) {
            50 -> DistanceRadius.M1000.value
            100 -> DistanceRadius.M2000.value
            else -> DistanceRadius.M500.value
        }
        val location = locationViewModel.getCurLocation()
        mapViewModel.filterDistance(param, location.latitude, location.longitude)
    }

    private fun TextView.setOnDistanceResetButtonTouchListener() = setOnTouchListener { _, event ->
        val action = event.action
        setChangeTextColorOnTouch(action)
        resetDistanceFilter(action)
        true
    }

    private fun resetDistanceFilter(event: Int) {
        binding.distanceSeekbar.initDistanceSeekBar()
        if(event == MotionEvent.ACTION_UP) {
            val location = locationViewModel.getCurLocation()
            mapViewModel.filterDistance(null, location.latitude, location.longitude)
        }
    }

    private fun IndicatorSeekBar.initDistanceSeekBar() {
        tickTextsColor(Resource.colorMatcheapGray)
        setProgress(DEFAULT_PROGRESS)
    }


    // !-- form here, functions about bookmark filter
    fun updateBookmarkFilter(isChecked: Boolean) {
        mapViewModel.filterBookmark(isChecked)
    }


    // !-- form here, functions about filter reset
    private fun TextView.setOnResetButtonTouchListener() = setOnTouchListener { _, event ->
        val action = event.action
        setChangeTextColorOnTouch(action)
        setChangeIconTintColorOnTouch(action)
        initViewDefaultState()
        mapViewModel.resetFilter()
        true
    }

    private fun initViewDefaultState() = binding.apply {
        tvRegion.text = ALL_SELECT
        btnAllSelect.isChecked = false
        setCategoryAllCheckState(false)
        distanceSeekbar.initDistanceSeekBar()
        btnBookmark.isChecked = false
    }


    override fun setOnBackPressedListener() = when(binding.view.visibility) {
        View.VISIBLE -> closeTransition()
        else -> handleExistDeepLinkNavigation()
    }

    private companion object {
        const val FLIP_0 = -1.0f
        const val FLIP_180 = 1.0f
    }

}