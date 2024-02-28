package com.sandy.matcheap.domain.model.menu

data class Menu(
    val storeId: String,
    val name: String?,
    val price: Int?
) {
    val priceStr: String get() = price.toString() + " 원"
}
