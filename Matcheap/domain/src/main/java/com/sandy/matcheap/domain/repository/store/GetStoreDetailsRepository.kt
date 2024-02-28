package com.sandy.matcheap.domain.repository.store

import com.sandy.matcheap.domain.model.store.StoreDetails

interface GetStoreDetailsRepository {

    suspend fun getStoreDetails(id: String): StoreDetails

}