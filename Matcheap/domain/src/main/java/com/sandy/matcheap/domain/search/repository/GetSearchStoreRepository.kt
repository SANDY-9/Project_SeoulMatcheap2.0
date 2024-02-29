package com.sandy.matcheap.domain.search.repository

import androidx.paging.PagingSource
import com.sandy.matcheap.domain.model.store.StoreItem

interface GetSearchStoreRepository {

    suspend fun requestSearchStore(param: String, curLat:Double, curLng:Double): PagingSource<Int, StoreItem>

}