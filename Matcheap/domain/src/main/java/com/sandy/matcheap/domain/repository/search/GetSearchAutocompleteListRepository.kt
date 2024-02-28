package com.sandy.matcheap.domain.repository.search

import com.sandy.matcheap.domain.model.store.StoreItem

interface GetSearchAutocompleteListRepository {

    suspend fun getAutoCompleteList(): List<StoreItem>

}