package com.sandy.matcheap.domain.model.map

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map_polygon")
data class Polygon(
    @PrimaryKey(autoGenerate = true)
    val num: Int = 0,
    val gu: String,
    val lat: Double,
    val lng: Double
)