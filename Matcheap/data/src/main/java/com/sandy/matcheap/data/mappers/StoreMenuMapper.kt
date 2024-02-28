package com.sandy.matcheap.data.mappers

import com.sandy.matcheap.data.remote.menu.model.MenuDTO
import com.sandy.matcheap.domain.model.menu.Menu

fun List<MenuDTO>.toDomain(): List<Menu> {
    return map {
        it.toDomain()
    }
}

<<<<<<< Updated upstream
fun MenuDTO.toDomain(): Menu {
=======
private fun MenuDTO.toDomain(): Menu {
>>>>>>> Stashed changes
    return Menu(
        storeId = id,
        name = name,
        price = price
    )
}