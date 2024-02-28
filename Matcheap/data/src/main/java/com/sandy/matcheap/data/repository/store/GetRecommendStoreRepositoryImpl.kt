package com.sandy.matcheap.data.repository.store

import com.sandy.matcheap.data.room.dao.StoreDao
import com.sandy.matcheap.domain.model.store.StoreItem
import com.sandy.matcheap.domain.repository.store.GetRecommendStoreRepository
import javax.inject.Inject

class GetRecommendStoreRepositoryImpl @Inject constructor(
    private val dao: StoreDao
): GetRecommendStoreRepository {
    override suspend fun getRecommendStore(): StoreItem {
        return dao.getRecommendStore()
    }
}