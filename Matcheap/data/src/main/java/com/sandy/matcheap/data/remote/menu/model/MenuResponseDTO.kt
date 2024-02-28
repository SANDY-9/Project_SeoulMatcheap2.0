package com.sandy.matcheap.data.remote.menu.model

import com.google.gson.annotations.SerializedName

data class MenuResponseDTO(
    @SerializedName("ListPriceModelStoreProductService")
    val listPriceModelStoreProductService: ListPriceModelStoreProductService
) {
    data class ListPriceModelStoreProductService(
        @SerializedName("row")
        val result: List<MenuDTO>?
    )
}
