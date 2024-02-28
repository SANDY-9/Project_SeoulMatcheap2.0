package com.sandy.matcheap.domain.repository.search

import androidx.paging.PagingSource
import com.sandy.matcheap.domain.model.store.StoreItem

interface GetSearchStoreRepository {

    suspend fun requestSearchStore(param: String, curLat:Double, curLng:Double): PagingSource<Int, StoreItem>

}