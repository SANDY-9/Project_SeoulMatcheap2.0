package com.sandy.matcheap.domain.store.repository

import androidx.paging.PagingSource
import com.sandy.matcheap.domain.model.menu.Menu
import com.sandy.matcheap.domain.model.store.StoreDetails
import com.sandy.matcheap.domain.model.store.StoreItem
import kotlinx.coroutines.flow.Flow

interface GetStoreListRepository {

    suspend fun getSurroundingStoreList(curLat: Double, curLng: Double): List<StoreItem>
    suspend fun getRandomStoreList(): List<StoreDetails>
    suspend fun getStoreListByGu(curLat: Double, curLng: Double, gu: String): PagingSource<Int, StoreItem>
    suspend fun getStoreListByCategory(curLat: Double, curLng: Double, r: Double): PagingSource<Int, StoreItem>
    suspend fun getStoresByFilter(
        code: List<String>,
        gu: String?,
        bookmarked: Boolean,
        centerX: Double,
        centerY: Double,
        r: Double
    ): List<StoreDetails>

}