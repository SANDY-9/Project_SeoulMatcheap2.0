package com.sandy.seoul_matcheap.ui.home

import com.sandy.matcheap.domain.model.store.StoreItem

data class SurroundingStoresState(
    val isLoading: Boolean = false,
    val data: List<StoreItem>? = null,
    val error: String = ""
)
