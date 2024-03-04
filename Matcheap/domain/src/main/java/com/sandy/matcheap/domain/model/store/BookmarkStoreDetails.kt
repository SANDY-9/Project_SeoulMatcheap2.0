package com.sandy.matcheap.domain.model.store

import com.sandy.matcheap.domain.model.menu.Menu


data class BookmarkStoreDetails(
    val id: String = "",
    val code: String = "",
    val	name: String = "",
    val	address: String = "",
    val content: String = "",
    val	photo: String = "",
    val	lat: Double = 0.0,
    val	lng: Double = 0.0,
    val bookmarked: Boolean? = null,
    val menus: List<Menu>? = null
)