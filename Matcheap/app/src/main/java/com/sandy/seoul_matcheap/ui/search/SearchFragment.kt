package com.sandy.seoul_matcheap.ui.search

import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.FragmentSearchBinding
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    private val searchViewModel : SearchViewModel by viewModels()

    override fun setupBinding(): FragmentSearchBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@SearchFragment
            viewModel = searchViewModel
            hasFocus = false
        }
    }

    private var searchHistoryAdapter : SearchHistoryAdapter? = null
    private var autoCompleteListAdapter : AutoCompleteListAdapter? = null
    override fun initGlobalVariables() {
        searchHistoryAdapter = SearchHistoryAdapter().apply {
            addOnItemClickListener()
        }
        autoCompleteListAdapter = AutoCompleteListAdapter(searchViewModel, viewLifecycleOwner).apply {
            addOnItemClickListener()
        }
    }

    override fun RecyclerView.Adapter<out RecyclerView.ViewHolder>.addOnItemClickListener() {
        when(this) {
            is SearchHistoryAdapter -> {
                setOnItemClickListener { navigateToSearchResult(it.name) }
                setOnRemoveListener { searchViewModel.removeHistory(it) }
            }
            is AutoCompleteListAdapter -> setOnItemClickListener {
                navigateToSearchResult(it.name)
            }
        }
    }

    override fun initView() = binding.run {
        btnBack.setOnBackButtonClickListener()
        evSearch.setup()
        rvHistory.addAdapter(searchHistoryAdapter)
        rvAutoComplete.addAdapter(autoCompleteListAdapter)
    }

    @Inject lateinit var inputManager: InputMethodManager
    private fun EditText.setup() {
        addOnEnterKeyPressedListener(inputManager)
        addOnFocusChangeListener()
    }
    private fun EditText.addOnFocusChangeListener() = setOnFocusChangeListener { _, hasFocus ->
        binding.hasFocus = hasFocus
    }

    override fun handleValidInput(param: String) {
        navigateToSearchResult(param)
    }


    override fun subscribeToObservers() = searchViewModel.run {
        historyList.observe(viewLifecycleOwner) {
            searchHistoryAdapter?.run {
                submitList(it)
            }
        }
        filteredAutoCompleteList.observe(viewLifecycleOwner) {
            autoCompleteListAdapter?.run {
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
            binding.evSearch.hasFocus() -> dropDownSoftKeyboard(inputManager)
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
        autoCompleteListAdapter = null
    }

}