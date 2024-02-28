package com.sandy.matcheap.domain.usecases.store

import com.sandy.matcheap.common.MESSAGE_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.model.store.StoreDetails
import com.sandy.matcheap.domain.repository.store.GetStoreDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStoreDetailsUseCase @Inject constructor(
    private val getStoreDetailsRepository: GetStoreDetailsRepository
) {

    operator fun invoke(id: String): Flow<Resource<StoreDetails>> = flow {
        emit(Resource.Loading())
        try {
            val details = getStoreDetailsRepository.getStoreDetails(id)
            emit(Resource.Success(details))
        } catch (e: Exception) {
            emit(Resource.Error(MESSAGE_ERROR))
        }
    }

}