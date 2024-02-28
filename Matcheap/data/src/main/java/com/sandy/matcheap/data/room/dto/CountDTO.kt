package com.sandy.matcheap.data.room.dto

import androidx.room.ColumnInfo

data class CountDTO(
    @ColumnInfo(name = "count(*)") val count: Int,
    val lat: Double,
    val lng: Double,
    val d: Float
)

data class StoreCountDTO(
    @ColumnInfo(name = "code") val category: String,
    @ColumnInfo(name = "count(*)") val count: Int
)