package com.sandy.seoul_matcheap.data.store.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class MenuResponse(
    @SerializedName("ListPriceModelStoreProductService")
    val listPriceModelStoreProductService: ListPriceModelStoreProductService
) {
    data class ListPriceModelStoreProductService(
        @SerializedName("row")
        val result: List<Menu>?
    )
}

@Entity(tableName = "store_menu")
data class Menu(
    @PrimaryKey(autoGenerate = true)
    val menuId: Int = 0,
    @SerializedName("SH_ID")
    val id: String,
    @SerializedName("IM_NAME")
    val name: String?,
    @SerializedName("IM_PRICE")
    val price: Int?
) {
    val priceStr: String get() = price.toString() + " 원"
}