package com.sandy.matcheap.data.repository.search

import com.sandy.matcheap.data.mappers.toDomain
import com.sandy.matcheap.data.room.dao.SearchDao
import com.sandy.matcheap.domain.search.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val dao: SearchDao
): SearchHistoryRepository {

    override fun getSearchHistoryList(): Flow<List<String>> {
        return dao.getSearchHistoryList().toDomain()
    }

    override suspend fun deleteSearchHistory(param: String) {
        dao.deleteSearchHistory(name = param)
    }

    override suspend fun clearSearchHistory() {
        dao.clearSearchHistory()
    }

}