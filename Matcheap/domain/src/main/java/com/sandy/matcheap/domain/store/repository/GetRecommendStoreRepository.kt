package com.sandy.matcheap.domain.store.repository

import com.sandy.matcheap.domain.model.store.StoreItem

interface GetRecommendStoreRepository {

    suspend fun getRecommendStore(): StoreItem

}