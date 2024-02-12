package com.sandy.seoul_matcheap.adapters

import android.content.Context
import android.view.*
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sandy.seoul_matcheap.data.store.dao.StoreItem
import com.sandy.seoul_matcheap.databinding.ItemRvStoreBinding
import com.sandy.seoul_matcheap.util.constants.PAGE_DISTANCE
import com.sandy.seoul_matcheap.util.module.GlideApp

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-06
 * @desc
 */
class StoreListAdapter : PagingDataAdapter<StoreItem, StoreListAdapter.ItemRvStoreViewHolder>(
    DIFF_UTIL
) {

    inner class ItemRvStoreViewHolder(val binding: ItemRvStoreBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemRvStoreViewHolder {
        val binding = ItemRvStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemRvStoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemRvStoreViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.binding.apply {
                store = item
                view.setOnClickListener {
                    onItemClickListener?.let {
                        it(item.id)
                    }
                }
                requestPreload(root.context, position)
            }
        }
    }

    private fun requestPreload(context: Context, position: Int) {
        val preloadPosition = position + PAGE_DISTANCE
        if(itemCount > preloadPosition) {
            val preloadUrl = getItem(preloadPosition)?.photo
            GlideApp.with(context).load(preloadUrl).preload()
        }
    }

    private var onItemClickListener: ((String) -> Unit)? = null
    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<StoreItem>() {
            override fun areItemsTheSame(oldItem: StoreItem, newItem: StoreItem)= oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: StoreItem, newItem: StoreItem) = oldItem == newItem
        }
    }

}
