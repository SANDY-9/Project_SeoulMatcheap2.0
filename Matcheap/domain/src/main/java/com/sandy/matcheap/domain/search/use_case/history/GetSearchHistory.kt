package com.sandy.matcheap.domain.search.use_case.history

import com.sandy.matcheap.domain.search.repository.SearchHistoryRepository
import javax.inject.Inject

class GetSearchHistory @Inject constructor(
    private val getSearchHistoryRepository: SearchHistoryRepository
){
    operator fun invoke() = getSearchHistoryRepository.getSearchHistoryList()
}