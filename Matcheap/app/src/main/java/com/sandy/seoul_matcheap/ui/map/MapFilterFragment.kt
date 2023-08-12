package com.sandy.seoul_matcheap.ui.map

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.FragmentMapFilterBinding
import com.sandy.seoul_matcheap.ui.common.*
import com.sandy.seoul_matcheap.ui.home.RegionSpinnerAdapter
import com.sandy.seoul_matcheap.util.constants.*
import com.warkiz.widget.*

class MapFilterFragment : BaseFragment<FragmentMapFilterBinding>(R.layout.fragment_map_filter) {

    override fun setupBinding(): FragmentMapFilterBinding = binding.apply {
        lifecycleOwner = viewLifecycleOwner
       // tvAddress.text = arguments?.getString(ADDRESS)
        fragment = this@MapFilterFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        openTransition()
        setCategoryChecked(false)
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

    private fun setCategoryChecked(isChecked: Boolean) = binding.apply {
        checkedHansik = isChecked
        checkedChina = isChecked
        checkedJapan = isChecked
        checkedOther = isChecked
        checkedWash = isChecked
        checkedBeauty = isChecked
        checkedHotel = isChecked
        checkedStore = isChecked
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


    // !-- form here, functions about region filter
    private fun RecyclerView.addAdapter() {
        adapter = RegionSpinnerAdapter(TYPE_REGION).apply {
            addOnItemClickListener()
        }
    }
    private fun RegionSpinnerAdapter.addOnItemClickListener() = setOnItemClickListener {
        updateRegionFilter(it)
    }

    // variables saving current filter state : region
    private var curRegion = ALL_SELECT
    private fun updateRegionFilter(param: String) {
        closeRegionSpinnerView()
        binding.tvRegion.text = param
        curRegion = param
        updateFilter()
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
        setCategoryChecked(isChecked)
        handleOnAllSelectCheckedChange(isChecked)
    }

    // variables saving current filter state : category
    // ALL_SELECT state : HANSIK_, CHINA_, JAPAN_, OTHER_, WASH_, BEAUTY_, HOTEL_, STORE_
    private val curCategorySet = mutableSetOf(HANSIK_, CHINA_, JAPAN_, OTHER_, WASH_, BEAUTY_, HOTEL_, STORE_)
    private fun handleOnAllSelectCheckedChange(isChecked: Boolean) {
        if(isChecked && curCategorySet.size == 8) return
        setCurCategory(isChecked, HANSIK_, CHINA_, JAPAN_, OTHER_, WASH_, BEAUTY_, HOTEL_, STORE_)
        updateFilter()
    }

    private fun setCurCategory(add: Boolean, vararg categories: String) = curCategorySet.run {
        when {
            add -> {
                if(size == 8) clear()
                addAll(categories)
            }
            else -> removeAll(categories.toSet())
        }
    }

    /**
     * this function plays a role of process that curChecked state change
     * if changed state is true, add to CurCategoryList
     * @param isChecked curChecked -> !isChecked : state after changing curChecked
     * @param code category name
     */
    fun updateCategoryFilter(isChecked: Boolean, code: String) {
        onCheckedChange(isChecked, code)
        setCurCategory(!isChecked, code)
        updateFilter()
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

    // variables saving current filter state : distance
    private var curDistance = NOT_DISTANCE
    private fun updateDistanceFilter(seekBar: IndicatorSeekBar) {
        curDistance = when(seekBar.progress) {
            50 -> KM_1
            100 -> KM_3
            else -> M_500
        }
        updateFilter()
    }

    private fun TextView.setOnDistanceResetButtonTouchListener() = setOnTouchListener { _, event ->
        val action = event.action
        setChangeTextColorOnTouch(action)
        resetDistanceFilter(action)
        true
    }

    private fun resetDistanceFilter(event: Int) = binding.run {
        distanceSeekbar.initDistanceSeekBar()
        if(curDistance == NOT_DISTANCE) return
        if(event == MotionEvent.ACTION_UP) {
            curDistance = NOT_DISTANCE
            updateFilter()
        }
    }

    private fun IndicatorSeekBar.initDistanceSeekBar() {
        tickTextsColor(Resource.colorMatcheapGray)
        setProgress(DEFAULT_PROGRESS)
    }


    // !-- form here, functions about bookmark filter
    // variables saving current filter state : distance, bookmark
    private var curBookmark = false
    fun updateBookmarkFilter(isChecked: Boolean) {
        curBookmark = isChecked
        updateFilter()
    }


    // this function finally send updated filtered to viewModel, and make apply to map
    private fun updateFilter() {
        val filter = Filter(curRegion, curCategorySet, curDistance, curBookmark)
        setFragmentResult(FILTER_, bundleOf(FILTER_ to filter))
    }


    // !-- form here, functions about filter reset
    private fun TextView.setOnResetButtonTouchListener() = setOnTouchListener { _, event ->
        changeResetButtonColorOnTouch(event.action)
        initViewDefaultState()
        updateFilterToReset()
        true
    }

    private fun TextView.changeResetButtonColorOnTouch(action: Int) = binding.run {
        setChangeTextColorOnTouch(action)
        setChangeIconTintColorOnTouch(action)
    }
    private fun TextView.setChangeIconTintColorOnTouch(action: Int) {
        compoundDrawableTintList = when(action) {
            MotionEvent.ACTION_UP -> Resource.matCheapGray
            else -> Resource.matCheapBlue
        }
    }

    private fun initViewDefaultState() = binding.apply {
        tvRegion.text = ALL_SELECT
        btnAllSelect.isChecked = false
        setCategoryChecked(false)
        distanceSeekbar.initDistanceSeekBar()
        btnBookmark.isChecked = false
    }

    private fun updateFilterToReset() {
        val isNotDefaultState = curRegion != ALL_SELECT || curCategorySet.size != 8 || curDistance != NOT_DISTANCE || curBookmark
        //if values of current filter not change, function terminate.
        if(isNotDefaultState) {
            initFilterDefaultValue()
            updateFilter()
        }
    }
    private fun initFilterDefaultValue() {
        // initialize region filter value
        curRegion = ALL_SELECT

        // initialize category filter value
        setCurCategory(true, HANSIK_, CHINA_, JAPAN_, OTHER_, WASH_, BEAUTY_, HOTEL_, STORE_)

        // initialize distance filter value
        curDistance = NOT_DISTANCE

        // initialize bookmark filter value
        curBookmark = false
    }

    override fun setOnBackPressedListener() = when(binding.view.visibility) {
        View.VISIBLE -> closeTransition()
        else -> handleExistDeepLinkNavigation()
    }

    private companion object {
        const val M_500 = 0.5
        const val KM_1 = 1.0
        const val KM_3 = 3.0
        const val FLIP_0 = -1.0f
        const val FLIP_180 = 1.0f
    }

}