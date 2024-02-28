package com.sandy.matcheap.data.mappers

import com.sandy.matcheap.data.room.dto.BookmarkedStoreDTO
import com.sandy.matcheap.data.room.dto.RandomStoreDTO
import com.sandy.matcheap.data.room.dto.StoreDetailsDTO
import com.sandy.matcheap.data.room.dto.StoreMapItemDTO
import com.sandy.matcheap.domain.model.menu.Menu
import com.sandy.matcheap.domain.model.store.StoreDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


fun StoreDetailsDTO.toDomain(): StoreDetails {
    return StoreDetails(
        id = store.id,
        code = store.code,
        name = store.name,
        gu = store.gu,
        address = store.address,
        tel = store.tel,
        time = store.time,
        closed = store.closed,
        reserve = store.reserve,
        parking = store.parking,
        content = store.content,
        photo = store.photo,
        lat = store.lat,
        lng = store.lng,
        bookmarked = bookmark?.bookmarked ?: false
    )
}

fun List<RandomStoreDTO>.toDomain(): List<StoreDetails> {
    return map {
        it.toDomain()
    }
}
private fun RandomStoreDTO.toDomain(): StoreDetails {
    return StoreDetails(
        id = id,
        code = code,
        name = name,
        address = address,
        content = content,
        photo = photo,
        lat = lat,
        lng = lng
    )
}

fun Flow<List<BookmarkedStoreDTO>>.toDomain(): Flow<List<Map<StoreDetails, List<Menu>?>>> {
    return map {
        it.toDomain()
    }
}
private fun List<BookmarkedStoreDTO>.toDomain(): List<Map<StoreDetails, List<Menu>?>> {
    return map {
        mapOf(
            StoreDetails(
                id = it.store.id,
                code = it.store.code,
                name = it.store.name,
                address = it.store.address,
                content = it.store.content,
                photo = it.store.photo,
                lat = it.store.lat,
                lng = it.store.lng,
                bookmarked = it.bookmark.bookmarked
            ) to it.menu?.toDomain()
        )
    }
}


fun List<StoreMapItemDTO>.toDomain(): List<StoreDetails> {
    return map {
        it.toDomain()
    }
}
private fun StoreMapItemDTO.toDomain(): StoreDetails {
    return StoreDetails(
        id = id,
        code = code,
        name = name,
        gu = gu,
        address = address,
        tel = "",
        time = "",
        closed = "",
        reserve = "",
        parking = "",
        content = "",
        photo = photo,
        lat = lat,
        lng = lng,
        bookmarked = bookmarked
    )
}