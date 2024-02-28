package com.sandy.matcheap.data.room.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import com.sandy.matcheap.data.remote.menu.model.MenuDTO
import com.sandy.matcheap.data.room.entity.Bookmark
import com.sandy.matcheap.domain.model.store.StoreDetails

data class StoreDetailsDTO(
    @Embedded
    val store: StoreDetails,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val bookmark: Bookmark?
)

data class RecommendStoreDTO(
    val id: String,
    val name: String
)

data class RandomStoreDTO(
    val id: String,
    val code: String,
    val	name: String,
    val	address: String,
    val content: String,
    val	photo: String,
    val lat: Double,
    val lng: Double
)

data class StoreMapItemDTO(
    val id: String,
    val code: String,
    val gu: String,
    val	name: String,
    val	address: String,
    val	photo: String,
    val	lat: Double,
    val	lng: Double,
    val d: Float = 0.0f,
    var bookmarked: Boolean = false
)

data class BookmarkedStoreDTO(
    @Embedded
    val bookmark: Bookmark,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val store: StoreDetails,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val menu: List<MenuDTO>?
)

data class AutoCompleteItemDTO(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "address") val address: String
)