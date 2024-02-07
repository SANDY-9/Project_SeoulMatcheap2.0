package com.sandy.seoul_matcheap.ui.search

import android.location.LocationManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.*
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.StoreItem
import com.sandy.seoul_matcheap.databinding.FragmentSearchResultBinding
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.ui.LocationViewModel
import com.sandy.seoul_matcheap.ui.store.StoreListAdapter
import com.sandy.seoul_matcheap.util.constants.DEFAULT_POSITION
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchResultFragment : BaseFragment<FragmentSearchResultBinding>(R.layout.fragment_search_result) {

    private val searchViewModel: SearchViewModel by viewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()

    override fun setupBinding(): FragmentSearchResultBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = searchViewModel
            location = locationViewModel
            hasFocus = false
        }
    }

    private val args: SearchResultFragmentArgs by navArgs()
    @Inject lateinit var locationManager: LocationManager
    override fun downloadData() {
        requestSearch(args.searchWord)
    }

    private fun requestSearch(param: String) {
        showProgressView(binding.progressView)
        binding.rvResultList.scrollToPosition(DEFAULT_POSITION)
        searchViewModel.run {
            input.value = param
            requestSearch(param, locationViewModel.getCurLocation())
        }
    }

    private var autoCompleteListAdapter : AutoCompleteListAdapter? = null
    private var storeListAdapter: StoreListAdapter? = null
    override fun initGlobalVariables() {
        autoCompleteListAdapter = AutoCompleteListAdapter(searchViewModel, viewLifecycleOwner).apply {
            addOnItemClickListener()
        }
        storeListAdapter = StoreListAdapter().apply {
            addOnItemClickListener()
            addOnLoadStateListener()
        }
    }
    override fun RecyclerView.Adapter<out RecyclerView.ViewHolder>.addOnItemClickListener() {
        when(this) {
            is AutoCompleteListAdapter -> setOnItemClickListener {
                dropDownSoftKeyboard(inputManager)
                requestSearch(it.name)
            }
            is StoreListAdapter -> setOnItemClickListener {
                navigateToStoreDetails(it)
            }
        }
    }
    private fun StoreListAdapter.addOnLoadStateListener() = addLoadStateListener {
        if (it.prepend.endOfPaginationReached) {
            handleResultCount(itemCount)
        }
    }

    private fun handleResultCount(itemCount: Int) {
        with(binding) {
            tvResult.text = "$itemCount 건"
            viewSearchFail.visibility = if (itemCount > DEFAULT_POSITION) View.GONE else View.VISIBLE
        }
    }

    override fun initView() = binding.run {
        btnBack.setOnBackButtonClickListener()
        evSearch.setup()
        rvResultList.addAdapter(storeListAdapter)
        rvAutoComplete.addAdapter(autoCompleteListAdapter)
        btnTop.addVisibleTopScrollButton(rvResultList)
    }

    @Inject lateinit var inputManager: InputMethodManager
    private fun EditText.setup() {
        addOnEnterKeyPressedListener(inputManager)
        addOnFocusChangeListener()
        binding.textField.setOnEndIconClickListener()
    }
    private fun EditText.addOnFocusChangeListener() = setOnFocusChangeListener { _, hasFocus ->
        binding.hasFocus = hasFocus
    }
    private fun TextInputLayout.setOnEndIconClickListener() = setEndIconOnClickListener {
        editText?.text?.clear()
        dropDownSoftKeyboard(inputManager)
    }

    override fun handleValidInput(param: String) {
        requestSearch(param)
    }

    private fun TextView.addVisibleTopScrollButton(recyclerView: RecyclerView) {
        val listener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                setVisibleForScroll(dy)
            }
        }
        recyclerView.addOnScrollListener(listener)
        setOnClickListener { recyclerView.smoothScrollToPosition(DEFAULT_POSITION) }
    }


    override fun subscribeToObservers() = searchViewModel.run {
        searchResultList.observe(viewLifecycleOwner) {
            updateStoreList(it)
        }
        filteredAutoCompleteList.observe(viewLifecycleOwner) {
            autoCompleteListAdapter?.run {
                submitList(it)
            }
        }
    }

    private fun updateStoreList(result: PagingData<StoreItem>) {
        storeListAdapter?.submitData(lifecycle, result)
        binding.rvResultList.scrollToPosition(DEFAULT_POSITION)
    }

    override fun setOnBackPressedListener() {
        when {
            binding.evSearch.hasFocus() -> dropDownSoftKeyboard(inputManager)
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
        autoCompleteListAdapter = null
        storeListAdapter = null
    }

}