package com.sandy.seoul_matcheap.adapters

import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.entity.Menu
import com.sandy.seoul_matcheap.databinding.ItemRvMenuBinding
import com.sandy.seoul_matcheap.adapters.BaseListAdapter

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2022-05-13
 * @desc
 */
class StoreMenuAdapter : BaseListAdapter<ItemRvMenuBinding, Menu>(R.layout.item_rv_menu) {

    override fun ItemRvMenuBinding.setBinding(item: Menu, position: Int) {
        menu = item
    }

}