package com.sandy.seoul_matcheap.ui.more.bookmark

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.BookmarkedStore
import com.sandy.seoul_matcheap.databinding.FragmentBookmarkBinding
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.ui.common.PagerAdapter
import com.sandy.seoul_matcheap.util.constants.CATEGORY_SIZE
import com.sandy.seoul_matcheap.util.constants.DEFAULT_POSITION
import com.sandy.seoul_matcheap.util.constants.TYPE_NORMAL_SCROLL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkFragment : BaseFragment<FragmentBookmarkBinding>(R.layout.fragment_bookmark) {

    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()

    override fun setupBinding(): FragmentBookmarkBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = bookmarkViewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showProgressView(binding.progressView)
        super.onViewCreated(view, savedInstanceState)
    }

    private var pagerAdapter: PagerAdapter<BookmarkListAdapter>? = null
    override fun initGlobalVariables() {
        val location = locationViewModel.getCurLocation()
        pagerAdapter = PagerAdapter(BookmarkListAdapter(location), location = location)
    }

    override fun initView() = binding.run {
        initPager()
        connectPagerWithTabLayout(tabLayout, pager, progressView)

        btnBack.setOnBackButtonClickListener()
    }

    private fun initPager() = binding.pager.apply {
        adapter = pagerAdapter
        setPagerPosition(DEFAULT_POSITION)
        setOnPageChangeListener(DEFAULT_POSITION)
    }

    private fun setPagerPosition(position: Int) {
        bookmarkViewModel.updateBookmarkCount(position.toString())
        setOnAdapterEventListeners(position)
    }
    private fun setOnAdapterEventListeners(position: Int) {
        pagerAdapter!!.adapter[position]?.apply {
            setOnItemClickListener {
                navigateToStoreDetails(it.store.id)
            }
            setOnRemoveBookmarkListener { id: String, code: String ->
                bookmarkViewModel.updateBookmark(id, code, false)
            }
        }
    }

    private fun ViewPager2.setOnPageChangeListener(defaultPosition: Int) {
        val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            private var oldPosition = defaultPosition
            override fun onPageSelected(newPosition: Int) {
                super.onPageSelected(newPosition)
                // because current state of old page is recycled, have to be restored old page to default scroll state.
                setOldPageScroll(oldPosition)
                oldPosition = newPosition

                setPagerPosition(newPosition)
            }
        }
        registerOnPageChangeCallback(onPageChangeCallback)
    }

    private fun setOldPageScroll(oldPosition: Int) {
        // whenever page change, old page have to is scrolled to top.
        pagerAdapter!!.initScroll(oldPosition, TYPE_NORMAL_SCROLL)
    }

    override fun subscribeToObservers() {
        bookmarkViewModel.bookmarkList.observe(viewLifecycleOwner) {
            handleBookmarkStoreListData(it)
        }
    }

    //why submit data to all of pagerAdapter is because thinking of swipe handling of viewpager.
    //when swiping, both sides of current page have to already be generated.
    private fun handleBookmarkStoreListData(stores: List<BookmarkedStore>) {
        repeat(CATEGORY_SIZE) { position ->
            pagerAdapter?.run {
                adapter[position]!!.submitList(getFilteredBookmarkStoreList(position, stores))
            }
        }
    }
    private fun getFilteredBookmarkStoreList(position: Int, stores: List<BookmarkedStore>) = when(position) {
        DEFAULT_POSITION -> stores
        else -> stores.filter { it.store.code == "$position" }
    }

    override fun setOnBackPressedListener(){
        handleExistDeepLinkNavigation()
    }
    override fun destroyGlobalVariables() {
        pagerAdapter = null
    }

}

