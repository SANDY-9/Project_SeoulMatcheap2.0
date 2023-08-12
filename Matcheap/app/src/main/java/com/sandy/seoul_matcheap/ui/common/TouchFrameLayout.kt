package com.sandy.seoul_matcheap.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-25
 * @desc
 */
class TouchFrameLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    var listener: OnTouchListener? = null
    interface OnTouchListener {
        fun onTouch()
    }
    fun setTouchListener(listener: OnTouchListener) {
        this.listener = listener
    }
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> listener?.onTouch()
        }
        return super.dispatchTouchEvent(event)
    }
}