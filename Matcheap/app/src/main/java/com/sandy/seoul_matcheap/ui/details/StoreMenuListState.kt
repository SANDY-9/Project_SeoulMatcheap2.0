package com.sandy.seoul_matcheap.ui.details

import com.sandy.matcheap.domain.model.menu.Menu
import com.sandy.matcheap.domain.model.store.StoreDetails

data class StoreMenuListState (
    val isLoading: Boolean = false,
    val data: List<Menu>? = null,
    val error: String = ""
)