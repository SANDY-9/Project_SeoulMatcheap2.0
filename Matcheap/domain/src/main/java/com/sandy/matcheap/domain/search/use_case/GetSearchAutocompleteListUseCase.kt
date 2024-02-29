package com.sandy.matcheap.domain.search.use_case

import com.sandy.matcheap.common.MESSAGE_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.model.store.StoreItem
import com.sandy.matcheap.domain.search.repository.GetSearchAutocompleteListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSearchAutocompleteListUseCase @Inject constructor(
    private val getSearchAutocompleteListRepository: GetSearchAutocompleteListRepository
) {
    operator fun invoke(): Flow<Resource<List<StoreItem>>> = flow {
        emit(Resource.Loading())
        try {
            val autoList = getSearchAutocompleteListRepository.getAutoCompleteList()
            emit(Resource.Success(autoList))
        } catch (e: Exception) {
            emit(Resource.Error(MESSAGE_ERROR))
        }
    }
}