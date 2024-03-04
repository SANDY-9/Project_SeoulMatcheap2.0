package com.sandy.seoul_matcheap.adapters

import android.content.Context
import android.location.Location
import com.sandy.matcheap.common.PAGE_DISTANCE
import com.sandy.matcheap.domain.model.store.BookmarkStoreDetails
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.ItemRvBookmarkBinding

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-09
 * @desc
 */
class BookmarkListAdapter(private val location: Location) : BaseListAdapter<ItemRvBookmarkBinding, BookmarkStoreDetails>(R.layout.item_rv_bookmark) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        requestPreload(holder.itemView.context, position, PAGE_DISTANCE)
    }

    override fun ItemRvBookmarkBinding.setBinding(item: BookmarkStoreDetails, position: Int) {
        location = this@BookmarkListAdapter.location
        bookmarkedStore = item
        item.menus?.let { menus ->
            rvMenu.adapter = StoreMenuAdapter().also {
                it.submitList(menus)
            }
        }
        btnRemove.setOnClickListener {
            onRemoveBookmark?.let {
                it(item.id)
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
        val preloadUrl = (getItem(preloadPosition) as BookmarkStoreDetails).photo
        preload(context, preloadUrl)
    }

    private var onRemoveBookmark: ((String) -> Unit)? = null
    fun setOnRemoveBookmarkListener(listener: (String) -> Unit) {
        onRemoveBookmark = listener
    }


}