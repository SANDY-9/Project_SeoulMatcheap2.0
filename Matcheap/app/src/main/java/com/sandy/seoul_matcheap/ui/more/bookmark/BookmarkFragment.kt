package com.sandy.seoul_matcheap.ui.more.bookmark

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.sandy.matcheap.common.CATEGORY_SIZE
import com.sandy.matcheap.common.DEFAULT_POSITION
import com.sandy.matcheap.common.TYPE_NORMAL_SCROLL
import com.sandy.matcheap.domain.model.store.BookmarkStoreDetails
import com.sandy.seoul_matcheap.MatcheapApplication.Companion.showToastMessage
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.adapters.BookmarkListAdapter
import com.sandy.seoul_matcheap.databinding.FragmentBookmarkBinding
import com.sandy.seoul_matcheap.extensions.connectPagerWithTabLayout
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.BaseFragment
import com.sandy.seoul_matcheap.adapters.PagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import showProgressView

@AndroidEntryPoint
class BookmarkFragment : BaseFragment<FragmentBookmarkBinding>(R.layout.fragment_bookmark) {

    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()

    override fun setupBinding(): FragmentBookmarkBinding {
        return binding.apply {
            fragment = this@BookmarkFragment
            lifecycleOwner = viewLifecycleOwner
            viewModel = bookmarkViewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.progressView.showProgressView()
        super.onViewCreated(view, savedInstanceState)
    }

    private var pagerAdapter: PagerAdapter<BookmarkListAdapter>? = null
    override fun initGlobalVariables() {
        val location = locationViewModel.getCurLocation()
        pagerAdapter = PagerAdapter(BookmarkListAdapter(location), location = location)
    }

    override fun initView() = binding.run {
        initPager()
        pager.connectPagerWithTabLayout(tabLayout, progressView, requireContext())
    }

    private fun initPager() = binding.pager.apply {
        adapter = pagerAdapter
        val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            private var oldPosition = DEFAULT_POSITION
            override fun onPageSelected(newPosition: Int) {
                super.onPageSelected(newPosition)
                // because current state of old page is recycled, have to be restored old page to default scroll state.
                setOldPageScroll(oldPosition)
                oldPosition = newPosition

                handleUpdatePositionState(newPosition)
            }
        }
        registerOnPageChangeCallback(onPageChangeCallback)
        handleUpdatePositionState(DEFAULT_POSITION)
    }

    private fun setOldPageScroll(oldPosition: Int, scroll: Int = TYPE_NORMAL_SCROLL) {
        // whenever page change, old page have to is scrolled to top.
        pagerAdapter!!.initScroll(oldPosition, scroll)
    }

    // 현재 포지션 상태에 따라 북마크 정보를 업데이트하고 페이저의 이벤트를 설정해줘야 한다.
    private fun handleUpdatePositionState(position: Int) {
        // code = position을 string으로 변환한 값
        bookmarkViewModel.updateBookmarkState(code = position.toString())

        // postion 상태에 따라 해당하는 어댑터의 이벤트 리스너를 설정한다.
        setOnPagerAdapterEventListeners(position)
    }

    private fun setOnPagerAdapterEventListeners(position: Int) {
        pagerAdapter!!.adapter[position]?.apply {
            setOnItemClickListener {
                navigateToStoreDetails(it.id)
            }
            setOnRemoveBookmarkListener { id: String ->
                bookmarkViewModel.deleteBookmark(id)
            }
        }
    }

    override fun subscribeToObservers() = bookmarkViewModel.run {
        bookmarkListState.observe(viewLifecycleOwner) {
            handleBookmarkListState(it)
        }
        bookmarkCountState.observe(viewLifecycleOwner) {
            handleBookmarkCountState(it)
        }
        deleteBookmarkState.observe(viewLifecycleOwner) {
            handleDeleteBookmarkState(it)
        }
    }

    //why submit data to all of pagerAdapter is because thinking of swipe handling of viewpager.
    //when swiping, both sides of current page have to already be generated.
    private fun handleBookmarkListState(state: BookmarkListState) {
        state.data?.let { stores ->
            repeat(CATEGORY_SIZE) { position ->
                pagerAdapter?.run {
                    adapter[position]!!.submitList(getFilteredBookmarkStoreList(position, stores))
                }
            }
        }
    }
    private fun getFilteredBookmarkStoreList(position: Int, stores: List<BookmarkStoreDetails>) = when(position) {
        DEFAULT_POSITION -> stores
        else -> stores.filter { it.code == "$position" }
    }

    private fun handleBookmarkCountState(state: BookmarkCountState) {
        binding.tvCount.text = state.data.toString()
    }

    private fun handleDeleteBookmarkState(state: DeleteBookmarkState) {
        if(state.data != null) showToastMessage(requireContext(), state.data)
        if(state.error.isNotBlank()) showToastMessage(requireContext(), state.error)
    }

    override fun setOnBackPressedListener(){
        handleExistDeepLinkNavigation()
    }
    override fun destroyGlobalVariables() {
        pagerAdapter = null
    }

}

