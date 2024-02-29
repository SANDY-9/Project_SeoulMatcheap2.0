package com.sandy.matcheap.domain.search.repository

import com.sandy.matcheap.domain.model.store.StoreItem

interface GetSearchAutocompleteListRepository {

    suspend fun getAutoCompleteList(): List<StoreItem>

}