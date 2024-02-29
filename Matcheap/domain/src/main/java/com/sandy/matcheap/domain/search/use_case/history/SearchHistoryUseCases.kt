package com.sandy.matcheap.domain.search.use_case.history

data class SearchHistoryUseCases(
    val getHistory: GetSearchHistory,
    val deleteHistory: DeleteSearchHistory,
    val clearHistory: ClearSearchHistory
)
