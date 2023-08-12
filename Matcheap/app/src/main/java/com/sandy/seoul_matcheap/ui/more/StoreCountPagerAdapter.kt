package com.sandy.seoul_matcheap.ui.more

import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.StoreTotalCount
import com.sandy.seoul_matcheap.databinding.ItemPagerStoreCountBinding
import com.sandy.seoul_matcheap.ui.common.BaseListAdapter

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-17
 * @desc
 */
class StoreCountPagerAdapter : BaseListAdapter<ItemPagerStoreCountBinding, StoreTotalCount>(R.layout.item_pager_store_count) {
    override fun ItemPagerStoreCountBinding.setBinding(item: StoreTotalCount, position: Int) {
        total = item
        root.setOnClickListener {
            onItemClick?.let { it(item) }
        }
    }

}