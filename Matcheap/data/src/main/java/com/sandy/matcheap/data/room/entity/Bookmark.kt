package com.sandy.matcheap.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-02
 * @desc
 */

@Entity(tableName = "store_bookmark")
data class Bookmark(
    @PrimaryKey
    val id: String,
    val code: String,
    var bookmarked: Boolean,
    var bookmarkDate: String
)