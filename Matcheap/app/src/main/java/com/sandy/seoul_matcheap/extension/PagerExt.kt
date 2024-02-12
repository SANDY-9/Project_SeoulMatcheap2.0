package com.sandy.seoul_matcheap.extension

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.util.constants.DEFAULT_POSITION
import showProgressView

fun ViewPager2.connectPagerWithTabLayout(
    tabLayout: TabLayout,
    progressView: FrameLayout,
    context: Context
) {
    val category = context.resources.getStringArray(R.array.category_name)
    TabLayoutMediator(tabLayout, this) { tab, position ->
        tab.apply {
            text = category[position]
            view.setOnClickListener {
                if(!isSelected) progressView.showProgressView()
            }
        }
    }.attach()
}

fun RecyclerView.anchorSmoothScrollToPosition(position: Int, anchorPosition: Int = 3) {
    layoutManager?.apply {
        when (this) {
            is LinearLayoutManager -> {
                val topItem = findFirstVisibleItemPosition()
                val distance = topItem - position
                val anchorItem = when {
                    distance > anchorPosition -> position + anchorPosition
                    distance < -anchorPosition -> position - anchorPosition
                    else -> topItem
                }
                if (anchorItem != topItem) scrollToPosition(anchorItem)
                post { smoothScrollToPosition(position) }
            }
            else -> smoothScrollToPosition(position)
        }
    }
}

fun ViewPager2.setPageAnimationDuration(item: Int, duration: Long) {
    val pxToDrag: Int = height * (item - currentItem)
    var previousValue = DEFAULT_POSITION
    ValueAnimator.ofInt(DEFAULT_POSITION, pxToDrag).run {
        addUpdateListener {
            val currentValue = it.animatedValue as Int
            val currentPxToDrag = -(currentValue - previousValue).toFloat()
            fakeDragBy(currentPxToDrag)
            previousValue = currentValue
        }
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) { beginFakeDrag() }
            override fun onAnimationEnd(animation: Animator) { endFakeDrag() }
            override fun onAnimationCancel(animation: Animator) { /* NO_OP */ }
            override fun onAnimationRepeat(animation: Animator) { /* NO_OP */ }
        })
        interpolator = AccelerateDecelerateInterpolator()
        this.duration = duration
        start()
    }
}