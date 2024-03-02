package com.sandy.seoul_matcheap.ui.home

import com.sandy.matcheap.domain.model.store.StoreDetails

data class RandomStoresState(
    val isLoading: Boolean = false,
    val data: List<StoreDetails>? = null,
    val error: String = ""
)
