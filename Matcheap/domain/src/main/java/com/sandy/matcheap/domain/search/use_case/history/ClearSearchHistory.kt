package com.sandy.matcheap.domain.search.use_case.history

import com.sandy.matcheap.domain.search.repository.SearchHistoryRepository
import javax.inject.Inject

class ClearSearchHistory @Inject constructor(
    private val getSearchHistoryRepository: SearchHistoryRepository
){
    suspend operator fun invoke() = getSearchHistoryRepository.clearSearchHistory()
}