package com.sandy.matcheap.domain.usecases.search.history

import com.sandy.matcheap.domain.repository.search.SearchHistoryRepository
import javax.inject.Inject

class GetSearchHistory @Inject constructor(
    private val getSearchHistoryRepository: SearchHistoryRepository
){
    operator fun invoke() = getSearchHistoryRepository.getSearchHistoryList()
}