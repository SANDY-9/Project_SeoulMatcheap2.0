package com.sandy.seoul_matcheap.ui.more.notice

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.matcheap.Notice
import com.sandy.seoul_matcheap.databinding.FragmentNoticeBinding
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.util.constants.MESSAGE_NETWORK_ERROR

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
            addOnItemClickListener()
            addOnLoadStateListener()
        }
    }

    override fun RecyclerView.Adapter<out RecyclerView.ViewHolder>.addOnItemClickListener() {
        when(this) {
            is NoticeTitleAdapter -> {
                setOnItemClickListener {
                    it.url?.let { url -> navigateToBrowser(url) }
                }
            }
        }
    }
    private fun NoticeTitleAdapter.addOnLoadStateListener() {
        addLoadStateListener { loadState ->
            when {
                loadState.append.endOfPaginationReached -> handleLoadSuccess()
                loadState.refresh is LoadState.Error -> handleLoadFail()
                else -> handleLoading()
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
        showToastMessage(MESSAGE_NETWORK_ERROR)
    }
    private fun handleLoading() {
        binding.progressView.root.visibility = View.VISIBLE
        binding.progressView.fail.visibility = View.GONE
    }

    override fun initView() = binding.run {
        btnBack.setOnBackButtonClickListener()
        recyclerView.adapter = noticeAdapter
        progressView.retry.setOnRetryButtonClickListener()
    }

    override fun TextView.setOnRetryButtonClickListener() {
        setOnClickListener {
            noticeAdapter!!.refresh()
        }
    }

    override fun subscribeToObservers() {
        noticeViewModel.noticeList.observe(viewLifecycleOwner) {
            handleNoticeList(it)
        }
    }
    private fun handleNoticeList(data: PagingData<Notice>) = noticeAdapter?.run {
        submitData(lifecycle, data)
    }

    override fun destroyGlobalVariables() {
        noticeAdapter = null
    }

}