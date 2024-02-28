package com.sandy.matcheap.domain.usecases.search.history

import com.sandy.matcheap.domain.repository.search.SearchHistoryRepository
import javax.inject.Inject

class DeleteSearchHistory @Inject constructor(
    private val getSearchHistoryRepository: SearchHistoryRepository
){
    suspend operator fun invoke(param: String) = getSearchHistoryRepository.deleteSearchHistory(param)
}