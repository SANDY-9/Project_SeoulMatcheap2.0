package com.sandy.seoul_matcheap.ui.more.notice

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.sandy.matcheap.domain.model.notice.Notice
import com.sandy.seoul_matcheap.MatcheapApplication.Companion.showToastMessage
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.adapters.NoticeTitleAdapter
import com.sandy.seoul_matcheap.databinding.FragmentNoticeBinding
import com.sandy.seoul_matcheap.ui.BaseFragment
import com.sandy.seoul_matcheap.util.constants.MESSAGE_NETWORK_ERROR
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NoticeFragment : BaseFragment<FragmentNoticeBinding>(R.layout.fragment_notice) {

    private val noticeViewModel: NoticeViewModel by viewModels()

    override fun setupBinding(): FragmentNoticeBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = noticeViewModel
        }
    }

    private var noticeAdapter: NoticeTitleAdapter? = null
    override fun initGlobalVariables() {
        noticeAdapter = NoticeTitleAdapter().apply {
            setOnItemClickListener {
                it.url?.let { url -> navigateToBrowser(url) }
            }
            addLoadStateListener { loadState ->
                when {
                    loadState.append.endOfPaginationReached -> handleLoadSuccess()
                    loadState.refresh is LoadState.Error -> handleLoadFail()
                    else -> handleLoading()
                }
            }
        }
    }

    // loadSuccess
    private fun handleLoadSuccess() {
        binding.progressView.root.visibility = View.GONE
    }
    // loadFail
    private fun handleLoadFail() {
        binding.progressView.fail.visibility = View.VISIBLE
        showToastMessage(requireContext(), MESSAGE_NETWORK_ERROR)
    }
    private fun handleLoading() {
        binding.progressView.root.visibility = View.VISIBLE
        binding.progressView.fail.visibility = View.GONE
    }

    override fun initView() = binding.run {
        recyclerView.adapter = noticeAdapter
        btnBack.setOnClickListener {
            setOnBackPressedListener()
        }
        progressView.retry.setOnClickListener {
            noticeAdapter!!.refresh()
        }
    }

    override fun subscribeToObservers() {
        lifecycleScope.launch {
            noticeViewModel.getNoticeList().collectLatest {
                handleNoticeList(it)
            }
        }
    }
    private fun handleNoticeList(data: PagingData<Notice>) = noticeAdapter?.run {
        submitData(lifecycle, data)
    }

    override fun destroyGlobalVariables() {
        noticeAdapter = null
    }

}