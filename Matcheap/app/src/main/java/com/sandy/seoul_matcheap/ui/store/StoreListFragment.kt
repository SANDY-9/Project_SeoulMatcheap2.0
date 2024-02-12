package com.sandy.seoul_matcheap.ui.store

import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.*
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.StoreItem
import com.sandy.seoul_matcheap.databinding.FragmentStoreListBinding
import com.sandy.seoul_matcheap.extension.connectPagerWithTabLayout
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.BaseFragment
import com.sandy.seoul_matcheap.adapters.PagerAdapter
import com.sandy.seoul_matcheap.adapters.StoreListAdapter
import com.sandy.seoul_matcheap.util.constants.*
import dagger.hilt.android.AndroidEntryPoint
import showProgressView

@AndroidEntryPoint
class StoreListFragment : BaseFragment<FragmentStoreListBinding>(R.layout.fragment_store_list) {

    private val storeViewModel: StoreViewModel by viewModels()
    private val args: StoreListFragmentArgs by navArgs()

    private val locationViewModel: LocationViewModel by activityViewModels()
    private val location: Location by lazy { locationViewModel.getCurLocation() }

    override fun setupBinding(): FragmentStoreListBinding = binding.apply {
        fragment = this@StoreListFragment
        lifecycleOwner = viewLifecycleOwner
        viewModel = storeViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.progressView.showProgressView()
        super.onViewCreated(view, savedInstanceState)
    }

    private var pagerAdapter: PagerAdapter<StoreListAdapter>? = null
    override fun initGlobalVariables() {
        pagerAdapter = PagerAdapter(StoreListAdapter()).apply {
            setOnScrollChangeListener {
                binding.btnTop.visibility = if(it > DEFAULT_POSITION) View.VISIBLE else View.GONE
            }
        }
    }

    override fun initView() = binding.run {
        val type = args.type
        val category = args.category

        pager.init(type, category, tabLayout, progressView)
        tvTitle.text = if(type == TYPE_CATEGORY) TYPE_CATEGORY_NAME else category
        tvGps.text = args.address
    }

    private fun ViewPager2.init(type: Int, category: String, tabLayout: TabLayout, progressView: FrameLayout,) {
        adapter = pagerAdapter
        connectPagerWithTabLayout(tabLayout, progressView, requireContext())

        val defaultPosition = if(type == TYPE_CATEGORY) category.toInt() else DEFAULT_POSITION
        setCurrentItem(defaultPosition, false)
        setPagerPosition(type, category, defaultPosition)
        setOnPageChangeListener(type, category, defaultPosition)
    }

    private fun setPagerPosition(type: Int, category: String, position: Int) {
        pagerAdapter!!.adapter[position]?.setOnItemClickListener {
            navigateToStoreDetails(it)
        }
        storeViewModel.updateStoreCount(type, category, position, location)
        binding.btnTop.setOnClickListener {
            pagerAdapter!!.initScroll(position, TYPE_SMOOTH_SCROLL)
        }
    }

    private fun ViewPager2.setOnPageChangeListener(type: Int, category: String, defaultPosition: Int) {
        val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            var oldPosition = defaultPosition
            override fun onPageSelected(newPosition: Int) {
                super.onPageSelected(newPosition)
                // because current state of old page is recycled, have to be restored old page to default scroll state.
                // whenever page change, old page have to is scrolled to top.
                pagerAdapter!!.initScroll(oldPosition, TYPE_NORMAL_SCROLL)
                oldPosition = newPosition

                setPagerPosition(type, category, newPosition)
            }
        }
        registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun subscribeToObservers() {
        storeViewModel.getStoreList(args.type, args.category, location).observe(viewLifecycleOwner) {
            handleStoreListData(it)
        }
    }

    //why submit data to all of pagerAdapter is because thinking of swipe handling of viewpager.
    //when swiping, both sides of current page have to already be generated.
    private fun handleStoreListData(storePagingData: PagingData<StoreItem>) {
        repeat(CATEGORY_SIZE) { position ->
            pagerAdapter?.run {
                adapter[position]!!.submitData(lifecycle, filteredStoreList(position, storePagingData))
            }
        }
    }

    private fun filteredStoreList(position: Int, stores: PagingData<StoreItem>) = when (position) {
        DEFAULT_POSITION -> stores
        else -> stores.filter { it.code == "$position" }
    }

    override fun destroyGlobalVariables() {
        pagerAdapter = null
    }

    private companion object {
        private const val TYPE_CATEGORY_NAME = "우리 동네 착한가격업소"
    }

}