package com.sandy.seoul_matcheap.adapters

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.extension.anchorSmoothScrollToPosition
import com.sandy.seoul_matcheap.util.constants.CATEGORY_SIZE
import com.sandy.seoul_matcheap.util.constants.DEFAULT_POSITION
import com.sandy.seoul_matcheap.util.constants.TYPE_NORMAL_SCROLL

typealias Adapter = RecyclerView.Adapter<out RecyclerView.ViewHolder>

class PagerAdapter<T: Adapter>(
    private val t: T,
    private val size: Int = CATEGORY_SIZE,
    private val location: Location? = null
) : RecyclerView.Adapter<PagerAdapter<T>.ItemPagerViewHolder>(){

    val adapter = hashMapOf<Int, T>().also {
        repeat(size) { position ->
            it[position] = when(t) {
                is StoreListAdapter -> StoreListAdapter()
                else -> BookmarkListAdapter(location!!)
            } as T
        }
    }.toMap()

    inner class ItemPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = run {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pager_view, parent, false)
        ItemPagerViewHolder(view)
    }

    private val recyclerViews = hashMapOf<Int, RecyclerView>()
    override fun onBindViewHolder(holder: ItemPagerViewHolder, position: Int) {
        holder.recyclerView.apply {
            recyclerViews[position] = this
            adapter = this@PagerAdapter.adapter[position]
            addOnScrollChangeListener()
        }
    }

    private fun RecyclerView.addOnScrollChangeListener() {
        val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScroll?.let { it(dy) }
            }
        }
        addOnScrollListener(onScrollListener)
    }

    private var onScroll: ((Int) -> Unit)? = null
    fun setOnScrollChangeListener(listener: (Int) -> Unit) {
        onScroll = listener
    }

    fun initScroll(position: Int, scrollType: Int) = recyclerViews[position]?.apply {
        when(scrollType) {
            TYPE_NORMAL_SCROLL -> scrollToPosition(DEFAULT_POSITION)
            else -> anchorSmoothScrollToPosition(DEFAULT_POSITION)
        }
    }

    override fun getItemCount() = size
}