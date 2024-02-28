package com.sandy.matcheap.domain.usecases.search.history

data class SearchHistoryUseCases(
    val getHistory: GetSearchHistory,
    val deleteHistory: DeleteSearchHistory,
    val clearHistory: ClearSearchHistory
)
