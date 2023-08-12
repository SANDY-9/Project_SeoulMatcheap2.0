package com.sandy.seoul_matcheap.ui.more

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.FragmentSeemoreBinding
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.util.constants.DEFAULT_POSITION
import com.sandy.seoul_matcheap.util.constants.TEAR_STOP_SEOUL_HOME_URL
import dagger.hilt.android.AndroidEntryPoint

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
            addOnItemClickListener()
        }
    }
    override fun RecyclerView.Adapter<out RecyclerView.ViewHolder>.addOnItemClickListener() {
        when(this) {
            is StoreCountPagerAdapter -> setOnItemClickListener {
                binding.pager.changePage()
            }
        }
    }

    override fun initView() {
        with(binding) {
            pager.setup()
            btnVisit.setOnTouchListener()
        }
    }

    private fun ViewPager2.setup() {
        post {
            setCurrentItem(savePosition, false)
        }
        orientation = ViewPager2.ORIENTATION_VERTICAL
        adapter = countPagerAdapter
        registerHandler(autoPageScrollHandler!!, HANDLER_DURATION, ::addAutoPageScrollHandler)
    }

    private fun addAutoPageScrollHandler() {
        binding.pager.changePage()
        registerHandler(autoPageScrollHandler!!, HANDLER_DURATION, ::addAutoPageScrollHandler)
    }
    private fun ViewPager2.changePage() {
        setCurrentItemWithDuration(
            item = if(currentItem == CODE_SIZE - 1) DEFAULT_POSITION else currentItem + 1,
            duration = 700L
        )
    }
    private fun ViewPager2.setCurrentItemWithDuration(item: Int, duration: Long) {
        val pxToDrag: Int = height * (item - currentItem)
        var previousValue = DEFAULT_POSITION
        ValueAnimator.ofInt(DEFAULT_POSITION, pxToDrag).run {
            addUpdateListener {
                val currentValue = it.animatedValue as Int
                val currentPxToDrag = -(currentValue - previousValue).toFloat()
                fakeDragBy(currentPxToDrag)
                previousValue = currentValue
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) { beginFakeDrag() }
                override fun onAnimationEnd(animation: Animator) { endFakeDrag() }
                override fun onAnimationCancel(animation: Animator) { /* NO_OP */ }
                override fun onAnimationRepeat(animation: Animator) { /* NO_OP */ }
            })
            interpolator = AccelerateDecelerateInterpolator()
            this.duration = duration
            start()
        }
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
    private fun getDestination(index: Int) = arrayOf(
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