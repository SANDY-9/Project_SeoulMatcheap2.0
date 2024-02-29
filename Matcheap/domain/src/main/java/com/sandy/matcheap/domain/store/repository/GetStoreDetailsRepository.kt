package com.sandy.matcheap.domain.store.repository

import com.sandy.matcheap.domain.model.store.StoreDetails

interface GetStoreDetailsRepository {

    suspend fun getStoreDetails(id: String): StoreDetails

}