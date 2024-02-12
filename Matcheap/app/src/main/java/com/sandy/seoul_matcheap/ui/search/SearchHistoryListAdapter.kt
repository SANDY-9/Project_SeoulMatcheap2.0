package com.sandy.seoul_matcheap.ui.search

import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.entity.SearchHistory
import com.sandy.seoul_matcheap.databinding.ItemRvHistoryBinding
import com.sandy.seoul_matcheap.ui.common.BaseListAdapter

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-09
 * @desc
 */
class SearchHistoryAdapter : BaseListAdapter<ItemRvHistoryBinding, SearchHistory>(R.layout.item_rv_history) {

    override fun ItemRvHistoryBinding.setBinding(item: SearchHistory, position: Int) {
        word = item.name
        btnRemove.setOnClickListener {
            onRemoveHistory?.let { it(item.name) }
        }
        root.setOnClickListener {
            onItemClick?.let { it(item) }
        }
    }

    private var onRemoveHistory: ((String) -> Unit)? = null
    fun setOnRemoveListener(listener: (String) -> Unit) {
        onRemoveHistory = listener
    }

}