package com.sandy.matcheap.data.mappers

import com.sandy.matcheap.data.room.dto.AutoCompleteItemDTO
import com.sandy.matcheap.data.room.entity.SearchHistory
import com.sandy.matcheap.domain.model.store.StoreItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Flow<List<SearchHistory>>.toDomain(): Flow<List<String>> {
    return map { list ->
        list.map { it.name }
    }
}

fun List<AutoCompleteItemDTO>.toDomain(): List<StoreItem> {
    return map {
        it.toDomain()
    }
}
private fun AutoCompleteItemDTO.toDomain(): StoreItem {
    return StoreItem(
        name = name,
        address = address
    )
}