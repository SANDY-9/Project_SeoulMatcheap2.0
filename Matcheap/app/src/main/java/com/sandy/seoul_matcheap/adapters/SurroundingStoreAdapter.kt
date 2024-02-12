package com.sandy.seoul_matcheap.adapters

import android.content.Context
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.StoreItem
import com.sandy.seoul_matcheap.databinding.ItemRvSurroundingBinding
import com.sandy.seoul_matcheap.util.constants.DEFAULT_POSITION

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-07
 * @desc
 */
class SurroundingStoreAdapter() : BaseListAdapter<ItemRvSurroundingBinding, StoreItem>(R.layout.item_rv_surrounding) {

    override fun ItemRvSurroundingBinding.setBinding(item: StoreItem, position: Int) {
        store = item
        root.apply {
            if(position == DEFAULT_POSITION) setPadding(48, 0, 0, 0)
            setOnClickListener { onItemClick?.let { it(item) } }
        }
    }

    override fun handlePreload(context: Context, preloadPosition: Int) {
        val preloadUrl = (getItem(preloadPosition) as StoreItem).photo
        preload(context, preloadUrl)
    }

}