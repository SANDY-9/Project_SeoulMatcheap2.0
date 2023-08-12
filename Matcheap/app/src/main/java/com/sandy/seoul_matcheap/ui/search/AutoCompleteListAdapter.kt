package com.sandy.seoul_matcheap.ui.search

import androidx.lifecycle.LifecycleOwner
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.AutoComplete
import com.sandy.seoul_matcheap.databinding.ItemRvAutocompleteBinding
import com.sandy.seoul_matcheap.ui.common.BaseListAdapter

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2022-05-15
 * @desc
 */
class AutoCompleteListAdapter(
    private val searchViewModel: SearchViewModel,
    private val owner: LifecycleOwner
    ) : BaseListAdapter<ItemRvAutocompleteBinding, AutoComplete>(R.layout.item_rv_autocomplete) {

    override fun ItemRvAutocompleteBinding.setBinding(item: AutoComplete, position: Int) {
        lifecycleOwner = owner
        auto = item
        viewModel = searchViewModel
        root.setOnClickListener {
            onItemClick?.let { it(item) }
        }
    }

}