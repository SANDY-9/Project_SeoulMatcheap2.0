package com.sandy.seoul_matcheap.ui.search

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import com.google.android.material.textfield.*
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.StoreItem
import com.sandy.seoul_matcheap.databinding.FragmentSearchResultBinding
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.store.StoreListAdapter
import com.sandy.seoul_matcheap.util.constants.DEFAULT_POSITION
import connectRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import showProgressView

@AndroidEntryPoint
class SearchResultFragment : SearchBaseFragment<FragmentSearchResultBinding>(R.layout.fragment_search_result) {

    private val locationViewModel: LocationViewModel by activityViewModels()

    override fun setupBinding(): FragmentSearchResultBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@SearchResultFragment
            viewModel = searchViewModel
            location = locationViewModel
            hasFocus = false
        }
    }

    private val args: SearchResultFragmentArgs by navArgs()
    override fun downloadData() {
        requestSearch(args.searchWord)
    }

    override fun requestSearch(param: String) {
        with(binding) {
            progressView.showProgressView()
            rvResultList.scrollToPosition(DEFAULT_POSITION)
        }
        searchViewModel.run {
            input.value = param
            requestSearch(param, locationViewModel.getCurLocation())
        }
    }

    private var storeListAdapter: StoreListAdapter? = null
    override fun initGlobalVariables() {
        super.initGlobalVariables()
        setAutoCompleteRecyclerView(binding.rvAutoComplete)
        storeListAdapter = StoreListAdapter().apply {
            setOnItemClickListener {
                navigateToStoreDetails(it)
            }
            addLoadStateListener {
                if (it.prepend.endOfPaginationReached) {
                    handleResultCount(itemCount)
                }
            }
        }
    }

    private fun handleResultCount(itemCount: Int) {
        with(binding) {
            tvResult.text = "$itemCount 건"
            viewSearchFail.visibility = if (itemCount > DEFAULT_POSITION) View.GONE else View.VISIBLE
        }
    }

    override fun initView() {
        super.initView()
        binding.run {
            rvResultList.adapter = storeListAdapter
            with(evSearch) {
                addOnEnterKeyPressedListener()
                setOnFocusChangeListener { _, hasFocus ->
                    binding.hasFocus = hasFocus
                }
            }
            textField.setOnEndIconClickListener()
            btnTop.connectRecyclerView(rvResultList)
        }
    }

    private fun TextInputLayout.setOnEndIconClickListener() = setEndIconOnClickListener {
        editText?.text?.clear()
        dropDownSoftKeyboard()
    }

    override fun subscribeToObservers() {
        super.subscribeToObservers()
        searchViewModel.searchResultList.observe(viewLifecycleOwner) {
            updateStoreList(it)
        }
    }

    private fun updateStoreList(result: PagingData<StoreItem>) {
        storeListAdapter?.submitData(lifecycle, result)
        binding.rvResultList.scrollToPosition(DEFAULT_POSITION)
    }

    override fun setOnBackPressedListener() {
        when {
            binding.evSearch.hasFocus() -> dropDownSoftKeyboard()
            else -> super.setOnBackPressedListener()
        }
    }


    override fun onStop() {
        super.onStop()
        // visibility of autoComplete RecyclerView is depend on hasFocus of Search EditText
        binding.evSearch.clearFocus()
    }

    override fun destroyGlobalVariables() {
        super.destroyGlobalVariables()
        storeListAdapter = null
    }

}