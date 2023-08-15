package com.sandy.seoul_matcheap.ui.more.bookmark

import android.content.Context
import android.location.Location
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.data.store.dao.BookmarkedStore
import com.sandy.seoul_matcheap.databinding.ItemRvBookmarkBinding
import com.sandy.seoul_matcheap.ui.common.BaseListAdapter
import com.sandy.seoul_matcheap.ui.store.StoreMenuAdapter
import com.sandy.seoul_matcheap.util.constants.PAGE_DISTANCE

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-09
 * @desc
 */
class BookmarkListAdapter(private val location: Location) : BaseListAdapter<ItemRvBookmarkBinding, BookmarkedStore>(R.layout.item_rv_bookmark) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        requestPreload(holder.itemView.context, position, PAGE_DISTANCE)
    }

    override fun ItemRvBookmarkBinding.setBinding(item: BookmarkedStore, position: Int) {
        location = this@BookmarkListAdapter.location
        bookmarkedStore = item
        item.menu?.let { menus ->
            rvMenu.adapter = StoreMenuAdapter().also {
                it.submitList(menus)
            }
        }
        btnRemove.setOnClickListener {
            onRemoveBookmark?.let {
                it(item.store.id, item.store.code)
            }
        }
        view.setOnClickListener {
            onItemClick?.let {
                it(item)
            }
        }
    }

    override fun requestPreload(context: Context, position: Int, preloadDistance: Int) {
        super.requestPreload(context, position, PAGE_DISTANCE)
    }

    override fun handlePreload(context: Context, preloadPosition: Int) {
        val preloadUrl = (getItem(preloadPosition) as BookmarkedStore).store.photo
        preload(context, preloadUrl)
    }

    private var onRemoveBookmark: ((String, String) -> Unit)? = null
    fun setOnRemoveBookmarkListener(listener: (String, String) -> Unit) {
        onRemoveBookmark = listener
    }


}