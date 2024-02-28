package com.sandy.matcheap.domain.repository.store

import com.sandy.matcheap.domain.model.store.StoreItem

interface GetRecommendStoreRepository {

    suspend fun getRecommendStore(): StoreItem

}