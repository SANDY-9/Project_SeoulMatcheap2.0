package com.sandy.seoul_matcheap.data.store.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-02-19
 * @desc
 */
@Entity(tableName = "map_polygon")
data class Polygon(
    @PrimaryKey(autoGenerate = true)
    val num: Int = 0,
    val gu: String,
    val lat: Double,
    val lng: Double
)
