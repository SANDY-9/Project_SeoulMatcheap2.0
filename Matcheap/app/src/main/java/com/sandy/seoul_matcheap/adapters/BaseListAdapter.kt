package com.sandy.seoul_matcheap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-25
 * @desc
 */
abstract class BaseListAdapter<B: ViewDataBinding, T>(@LayoutRes private val layoutId: Int) : ListAdapter<Any, BaseListAdapter<B, T>.ViewHolder>(
    DIFF_UTIL
) {
    inner class ViewHolder(val binding: B) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<B>(LayoutInflater.from(parent.context), layoutId, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as T?
        item?.let {
            holder.binding.setBinding(it, position)
            requestPreload(holder.itemView.context, position)
        }
    }

    abstract fun B.setBinding(item: T, position: Int)

    protected open fun requestPreload(context: Context, position: Int, preloadDistance: Int = 1) {
        val preloadPosition = position + preloadDistance
        if(itemCount > preloadPosition) {
            handlePreload(context, preloadPosition)
        }
    }
    protected open fun handlePreload(context: Context, preloadPosition: Int) =  Unit
    protected fun preload(context: Context, preloadUrl: String) = Glide.with(context).load(preloadUrl).preload()

    protected var onItemClick: ((T) -> Unit)? = null
    fun setOnItemClickListener(listener: (T) -> Unit) {
        onItemClick = listener
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return true
            }
        }
    }

}
