package com.sandy.seoul_matcheap.ui.search

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.sandy.seoul_matcheap.MatcheapApplication
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.util.constants.MESSAGE_SEARCH_WARNING
import dropDownSoftKeyboard

abstract class SearchBaseFragment<B: ViewDataBinding>(@LayoutRes private val layoutId: Int): BaseFragment<B>(layoutId) {

    protected val searchViewModel : SearchViewModel by viewModels()

    private var autoCompleteListAdapter : AutoCompleteListAdapter? = null
    private var rvAutoComplete: RecyclerView? = null
    private var inputManager: InputMethodManager? = null

    protected fun setAutoCompleteRecyclerView(rvAutoComplete: RecyclerView) {
        this.rvAutoComplete = rvAutoComplete
    }

    override fun initGlobalVariables() {
        inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        autoCompleteListAdapter = AutoCompleteListAdapter(searchViewModel, viewLifecycleOwner).apply {
            setOnItemClickListener {
                dropDownSoftKeyboard()
                requestSearch(it.name)
            }
        }
    }

    abstract fun requestSearch(param: String)

    override fun initView() {
        rvAutoComplete?.adapter = autoCompleteListAdapter
    }

    protected fun EditText.addOnEnterKeyPressedListener() =
        setOnEditorActionListener { _, _, _ ->
            dropDownSoftKeyboard()
            val param = text.toString().trim()
            return@setOnEditorActionListener param.isNotBlank().also { isNotBlank ->
                when {
                    isNotBlank -> requestSearch(param)
                    else -> MatcheapApplication.showToastMessage(context, MESSAGE_SEARCH_WARNING)
                }
            }
        }

    protected fun dropDownSoftKeyboard() {
        requireActivity().dropDownSoftKeyboard(inputManager)
    }

    override fun subscribeToObservers() {
        searchViewModel.filteredAutoCompleteList.observe(viewLifecycleOwner) {
            autoCompleteListAdapter?.run {
                submitList(it)
            }
        }
    }

    override fun destroyGlobalVariables() {
        super.destroyGlobalVariables()
        autoCompleteListAdapter = null
        rvAutoComplete = null
        inputManager = null
    }

}