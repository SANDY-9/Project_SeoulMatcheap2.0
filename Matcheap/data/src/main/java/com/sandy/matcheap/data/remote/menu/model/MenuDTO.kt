package com.sandy.matcheap.data.remote.menu.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "store_menu")
data class MenuDTO(
    @PrimaryKey(autoGenerate = true)
    val menuId: Int = 0,
    @SerializedName("SH_ID")
    val id: String,
    @SerializedName("IM_NAME")
    val name: String?,
    @SerializedName("IM_PRICE")
    val price: Int?
)