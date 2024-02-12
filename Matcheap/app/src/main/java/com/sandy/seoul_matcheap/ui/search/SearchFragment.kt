package com.sandy.seoul_matcheap.ui.search

import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : SearchBaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    override fun setupBinding(): FragmentSearchBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@SearchFragment
            viewModel = searchViewModel
            hasFocus = false
        }
    }

    private var searchHistoryAdapter : SearchHistoryAdapter? = null
    override fun initGlobalVariables() {
        super.initGlobalVariables()
        setAutoCompleteRecyclerView(binding.rvAutoComplete)
        searchHistoryAdapter = SearchHistoryAdapter().apply {
            setOnItemClickListener { requestSearch(it.name) }
            setOnRemoveListener { searchViewModel.removeHistory(it) }
        }
    }

    override fun initView() {
        super.initView()
        binding.run {
            rvHistory.adapter = searchHistoryAdapter
            with(evSearch) {
                addOnEnterKeyPressedListener()
                setOnFocusChangeListener { _, hasFocus ->
                    binding.hasFocus = hasFocus
                }
            }
        }
    }

    override fun requestSearch(param: String) {
        navigateToSearchResult(param)
    }


    override fun subscribeToObservers(){
        super.subscribeToObservers()
        searchViewModel.historyList.observe(viewLifecycleOwner) {
            searchHistoryAdapter?.run {
                submitList(it)
            }
        }
    }

    fun navigateToSearchResult(param: String) {
        val action = SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(
            searchWord = param
        )
        navigateDirections(action)
        binding.evSearch.text?.clear()
    }

    override fun setOnBackPressedListener() {
        when {
            binding.evSearch.hasFocus() -> dropDownSoftKeyboard()
            else -> handleExistDeepLinkNavigation()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.evSearch.text?.clear()
    }

    override fun destroyGlobalVariables() {
        super.destroyGlobalVariables()
        searchHistoryAdapter = null
    }

}