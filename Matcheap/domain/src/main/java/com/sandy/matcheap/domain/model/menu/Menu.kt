package com.sandy.matcheap.domain.model.menu

<<<<<<< Updated upstream
data class Menu(
    val storeId: String,
=======
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_menu")
data class Menu(
    @PrimaryKey(autoGenerate = true)
    val menuId: Int = 0,
    val id: String,
>>>>>>> Stashed changes
    val name: String?,
    val price: Int?
) {
    val priceStr: String get() = price.toString() + " 원"
}
