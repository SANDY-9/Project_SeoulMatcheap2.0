package com.sandy.seoul_matcheap.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sandy.seoul_matcheap.data.matcheap.Notice
import com.sandy.seoul_matcheap.databinding.ItemRvNoticeBinding

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-24
 * @desc
 */
class NoticeTitleAdapter : PagingDataAdapter<Notice, NoticeTitleAdapter.MyViewHolder>(DIFF_UTIL) {
    inner class MyViewHolder(val binding: ItemRvNoticeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemRvNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.binding.run {
                notice = item
                root.setOnClickListener {
                    onItemClick?.let {
                        it(item)
                    }
                }
            }
        }
    }

    private var onItemClick: ((Notice) -> Unit)? = null
    fun setOnItemClickListener(listener: (Notice) -> Unit) {
        onItemClick = listener
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Notice>() {
            override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    }
}