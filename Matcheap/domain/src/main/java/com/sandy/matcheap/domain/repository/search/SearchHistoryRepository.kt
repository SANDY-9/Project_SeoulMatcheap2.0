package com.sandy.matcheap.domain.repository.search

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {

    fun getSearchHistoryList(): Flow<List<String>>
    suspend fun deleteSearchHistory(param: String)
    suspend fun clearSearchHistory()

}