package com.sandy.seoul_matcheap.ui.more

import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import changeScale
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.FragmentSeemoreBinding
import com.sandy.seoul_matcheap.extension.setPageAnimationDuration
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.util.constants.DEFAULT_POSITION
import com.sandy.seoul_matcheap.util.constants.TEAR_STOP_SEOUL_HOME_URL
import dagger.hilt.android.AndroidEntryPoint
import setChangeTextColorOnTouch

@AndroidEntryPoint
class SeeMoreFragment : BaseFragment<FragmentSeemoreBinding>(R.layout.fragment_seemore) {

    private val moreViewModel: SeeMoreViewModel by viewModels()

    override fun setupBinding(): FragmentSeemoreBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@SeeMoreFragment
            viewModel = moreViewModel
        }
    }

    private var savePosition = DEFAULT_POSITION

    private var autoPageScrollHandler: Handler? = null
    private var countPagerAdapter : StoreCountPagerAdapter? = null
    override fun initGlobalVariables() {
        autoPageScrollHandler = Handler(Looper.getMainLooper())
        countPagerAdapter = StoreCountPagerAdapter().apply {
            setOnItemClickListener {
                binding.pager.changePage()
            }
        }
    }

    override fun initView() = binding.run {
        pager.init()
        btnVisit.setOnTouchListener()
    }

    private fun ViewPager2.init() {
        post {
            setCurrentItem(savePosition, false)
        }
        orientation = ViewPager2.ORIENTATION_VERTICAL
        adapter = countPagerAdapter
        registerHandler(autoPageScrollHandler!!, HANDLER_DURATION / 2, ::addAutoPageScrollHandler)
    }

    private fun registerHandler(handler: Handler = Handler(Looper.getMainLooper()), delay: Long, func: () -> Unit) {
        handler.postDelayed(func, delay)
    }

    private fun addAutoPageScrollHandler() {
        binding.pager.changePage()
        registerHandler(autoPageScrollHandler!!, HANDLER_DURATION, ::addAutoPageScrollHandler)
    }

    private fun ViewPager2.changePage() {
        setPageAnimationDuration(
            item = if(currentItem == CODE_SIZE - 1) DEFAULT_POSITION else currentItem + 1,
            duration = 700L
        )
    }

    private fun TextView.setOnTouchListener() = setOnTouchListener { _, event ->
        setChangeTextColorOnTouch(event.action)
        if(event.action == MotionEvent.ACTION_UP) navigateToBrowser(TEAR_STOP_SEOUL_HOME_URL)
        true
    }

    override fun subscribeToObservers() {
        moreViewModel.storeCounts.observe(viewLifecycleOwner) {
            countPagerAdapter?.run { submitList(it) }
        }
    }

    fun navigateToDestination(v: View, index: Int) {
        v.changeScale()
        findNavController().navigate(getDestination(index))
        savePosition = binding.pager.currentItem
    }
    private fun getDestination(index: Int) = intArrayOf(
        R.id.action_seeMoreFragment_to_bookMarkFragment,
        R.id.action_seeMoreFragment_to_settingsFragment,
        R.id.action_seeMoreFragment_to_noticeFragment,
        R.id.action_seeMoreFragment_to_infoFragment
    )[index]

    override fun onStop() {
        super.onStop()
        autoPageScrollHandler?.removeCallbacksAndMessages(null)
        autoPageScrollHandler = null
    }

    override fun destroyGlobalVariables() {
        countPagerAdapter = null
    }

    companion object {
        private const val CODE_SIZE = 8
        private const val HANDLER_DURATION = 2700L
    }

}