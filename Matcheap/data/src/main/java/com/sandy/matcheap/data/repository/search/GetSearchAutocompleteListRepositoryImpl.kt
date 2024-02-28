package com.sandy.matcheap.data.repository.search

import com.sandy.matcheap.data.mappers.toDomain
import com.sandy.matcheap.data.room.dao.SearchDao
import com.sandy.matcheap.domain.model.store.StoreItem
import com.sandy.matcheap.domain.repository.search.GetSearchAutocompleteListRepository
import javax.inject.Inject

class GetSearchAutocompleteListRepositoryImpl @Inject constructor(
    private val dao: SearchDao
): GetSearchAutocompleteListRepository {

    override suspend fun getAutoCompleteList(): List<StoreItem> {
        return dao.getAutoCompleteList().toDomain()
    }

}