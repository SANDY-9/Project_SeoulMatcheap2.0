package com.sandy.matcheap.data.repository.search

import androidx.paging.PagingSource
import androidx.room.Transaction
import com.sandy.matcheap.data.room.dao.SearchDao
import com.sandy.matcheap.data.room.entity.SearchHistory
import com.sandy.matcheap.domain.model.store.StoreItem
import com.sandy.matcheap.domain.repository.search.GetSearchStoreRepository
import java.time.LocalDateTime

class GetSearchStoreRepositoryImpl constructor(
    private val dao: SearchDao
): GetSearchStoreRepository {

    @Transaction
    override suspend fun requestSearchStore(
        param: String,
        curLat: Double,
        curLng: Double
    ): PagingSource<Int, StoreItem> {
        val searchHistory = SearchHistory(param.trim(), "${LocalDateTime.now()}")
        dao.insertSearchHistory(searchHistory)
        return dao.requestSearchStore(param, curLat, curLng)
    }

}