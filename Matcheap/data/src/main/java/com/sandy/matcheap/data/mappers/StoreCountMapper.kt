package com.sandy.matcheap.data.mappers

import com.sandy.matcheap.data.room.dto.CountDTO
import com.sandy.matcheap.data.room.dto.StoreCountDTO


fun CountDTO.toInt(): Int {
    return count
}

fun List<StoreCountDTO>.toDomain(): Map<String, Int> {
    return associate { it.category to it.count }
}