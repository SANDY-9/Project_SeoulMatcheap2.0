package com.sandy.matcheap.domain.model.store

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "store_info")
data class StoreDetails(
    @PrimaryKey
    val id: String = "",
    val code: String = "",
    val	name: String = "",
    val gu: String = "",
    val	address: String = "",
    val	tel: String = "",
    val	time: String = "",
    val	closed: String = "",
    val reserve: String = "",
    val	parking: String = "",
    val content: String = "",
    val	photo: String = "",
    val	lat: Double = 0.0,
    val	lng: Double = 0.0,
    @Ignore
    var bookmarked: Boolean? = null
): Serializable
