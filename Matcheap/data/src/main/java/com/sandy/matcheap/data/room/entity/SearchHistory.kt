package com.sandy.matcheap.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey
    val name: String,
    var date: String
)