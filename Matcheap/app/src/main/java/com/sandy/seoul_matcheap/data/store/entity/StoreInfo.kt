package com.sandy.seoul_matcheap.data.store.entity

import androidx.room.*

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-22
 * @desc store details entity
 */

@Entity(tableName = "store_info")
data class StoreInfo(
    @PrimaryKey
    val id: String,
    val code: String,
    val	name: String,
    val gu: String,
    val	address: String,
    val	tel: String,
    val	time: String,
    val	closed: String,
    val reserve: String,
    val	parking: String,
    val content: String,
    val	photo: String,
    val	lat: Double,
    val	lng: Double
)