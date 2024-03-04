package com.sandy.seoul_matcheap.ui.more.bookmark

import com.sandy.matcheap.domain.model.store.BookmarkStoreDetails

data class BookmarkListState(
    val isLoading: Boolean = false,
    val data: List<BookmarkStoreDetails>? = null,
    val error: String = ""
)
