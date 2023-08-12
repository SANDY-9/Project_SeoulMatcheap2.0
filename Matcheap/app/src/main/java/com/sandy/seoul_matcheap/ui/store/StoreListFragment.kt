package com.sandy.seoul_matcheap.ui.store

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.*
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.StoreListItem
import com.sandy.seoul_matcheap.databinding.FragmentStoreListBinding
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.ui.common.PagerAdapter
import com.sandy.seoul_matcheap.util.constants.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreListFragment : BaseFragment<FragmentStoreListBinding>(R.layout.fragment_store_list) {

    private val storeViewModel: StoreViewModel by viewModels()
    private val args : StoreListFragmentArgs by navArgs()

    override fun setupBinding(): FragmentStoreListBinding = binding.apply {
        lifecycleOwner = viewLifecycleOwner
        viewModel = storeViewModel
    }

    override fun downloadData() {
        storeViewModel.downloadStoreList(args.type, args.category)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showProgressView(binding.progressView)
        super.onViewCreated(view, savedInstanceState)
    }

    private var pagerAdapter: PagerAdapter<StoreListAdapter>? = null
    override fun initGlobalVariables() {
        pagerAdapter = PagerAdapter(StoreListAdapter()).apply {
            setOnScrollChangeListener {
                binding.btnTop.setVisibleForScroll(it)
            }
        }
    }

    override fun initView() = args.run {
        initPager(type, category)

        with(binding) {
            connectPagerWithTabLayout(tabLayout, pager, progressView)
            btnBack.setOnBackButtonClickListener()
            tvTitle.setTitleText(type, category)
            tvGps.text = address
        }
    }

    private fun initPager(type: Int, category: String) = binding.pager.apply {
        adapter = pagerAdapter

        val defaultPosition = if(args.type == TYPE_CATEGORY) args.category.toInt() else DEFAULT_POSITION
        setCurrentItem(defaultPosition, false)
        setPagerPosition(type, category, defaultPosition)
        setOnPageChangeListener(type, category, defaultPosition)
    }

    private fun setPagerPosition(type: Int, category: String, position: Int) {
        pagerAdapter!!.adapter[position]?.addOnItemClickListener()
        storeViewModel.updateStoreCount(type, category, position)
        binding.btnTop.setOnTopScrollButtonClickListener(position)
    }

    override fun RecyclerView.Adapter<out RecyclerView.ViewHolder>.addOnItemClickListener() {
        when(this) {
            is StoreListAdapter -> setOnItemClickListener {
                navigateToStoreDetails(it)
            }
        }
    }

    private fun TextView.setOnTopScrollButtonClickListener(position: Int) = setOnClickListener {
        pagerAdapter!!.initScroll(position, TYPE_SMOOTH_SCROLL)
    }

    private fun ViewPager2.setOnPageChangeListener(type: Int, category: String, defaultPosition: Int) {
        val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            var oldPosition = defaultPosition
            override fun onPageSelected(newPosition: Int) {
                super.onPageSelected(newPosition)
                // because current state of old page is recycled, have to be restored old page to default scroll state.
                setOldPageScroll(oldPosition)
                oldPosition = newPosition

                setPagerPosition(type, category, newPosition)
            }
        }
        registerOnPageChangeCallback(onPageChangeCallback)
    }
    private fun setOldPageScroll(oldPosition:Int) {
        // whenever page change, old page have to is scrolled to top.
        pagerAdapter!!.initScroll(oldPosition, TYPE_NORMAL_SCROLL)
    }

    private fun TextView.setTitleText(type: Int, name: String) {
        text = if(type == TYPE_CATEGORY) TYPE_CATEGORY_NAME else name
    }

    override fun subscribeToObservers() {
        storeViewModel.storeList.observe(viewLifecycleOwner) {
            handleStoreListData(it)
        }
    }

    //why submit data to all of pagerAdapter is because thinking of swipe handling of viewpager.
    //when swiping, both sides of current page have to already be generated.
    private fun handleStoreListData(storePagingData: PagingData<StoreListItem>) {
        repeat(CATEGORY_SIZE) { position ->
            pagerAdapter?.run {
                adapter[position]!!.submitData(lifecycle, filteredStoreList(position, storePagingData))
            }
        }
    }
    private fun filteredStoreList(position: Int, stores: PagingData<StoreListItem>) = when (position) {
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