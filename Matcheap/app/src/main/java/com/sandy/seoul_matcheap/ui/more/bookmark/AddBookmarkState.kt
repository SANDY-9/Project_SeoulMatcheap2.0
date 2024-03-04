package com.sandy.seoul_matcheap.ui.more.bookmark


data class AddBookmarkState(
    val isLoading: Boolean = false,
    val data: String? = null,
    val error: String = ""
)