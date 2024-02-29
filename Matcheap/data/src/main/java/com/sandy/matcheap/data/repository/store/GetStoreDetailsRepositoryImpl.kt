package com.sandy.matcheap.data.repository.store

import com.sandy.matcheap.data.mappers.toDomain
import com.sandy.matcheap.data.room.dao.StoreDao
import com.sandy.matcheap.domain.model.store.StoreDetails
import com.sandy.matcheap.domain.store.repository.GetStoreDetailsRepository
import javax.inject.Inject

class GetStoreDetailsRepositoryImpl @Inject constructor(
    private val dao: StoreDao
): GetStoreDetailsRepository {

    override suspend fun getStoreDetails(id: String): StoreDetails {
        val response = dao.getStoreDetails(id)
        return response.toDomain()
    }

}