package com.sandy.seoul_matcheap.ui.more.bookmark


data class BookmarkCountState(
    val isLoading: Boolean = false,
    val data: Int? = null,
    val error: String = ""
)