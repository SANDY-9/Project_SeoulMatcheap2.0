package com.sandy.seoul_matcheap.ui.details

import com.sandy.matcheap.domain.model.store.StoreDetails

data class StoreDetailsState (
    val isLoading: Boolean = false,
    val data: StoreDetails? = null,
    val error: String = ""
)