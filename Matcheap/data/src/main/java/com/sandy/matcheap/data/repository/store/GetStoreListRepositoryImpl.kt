package com.sandy.matcheap.data.repository.store

import androidx.paging.PagingSource
import com.sandy.matcheap.data.mappers.toDomain
import com.sandy.matcheap.data.room.dao.StoreDao
import com.sandy.matcheap.domain.model.menu.Menu
import com.sandy.matcheap.domain.model.store.StoreDetails
import com.sandy.matcheap.domain.model.store.StoreItem
import com.sandy.matcheap.domain.repository.store.GetStoreListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoreListRepositoryImpl @Inject constructor(
    private val storeDao: StoreDao
): GetStoreListRepository {

    override suspend fun getSurroundingStoreList(curLat: Double, curLng: Double): List<StoreItem> {
        return storeDao.getSurroundingStores(curLat, curLng)
    }

    override suspend fun getRandomStoreList(): List<StoreDetails> {
        return storeDao.getRandomStores().toDomain()
    }

    override suspend fun getStoreListByGu(
        curLat: Double,
        curLng: Double,
        gu: String
    ): PagingSource<Int, StoreItem> {
        return storeDao.getStoreListByRegion(curLat, curLng, gu)
    }

    override suspend fun getStoreListByCategory(
        curLat: Double,
        curLng: Double,
        r: Double
    ): PagingSource<Int, StoreItem> {
        return storeDao.getStoreListByCategory(curLat, curLng, r)
    }

    override suspend fun getBookmarkStoreList(): Flow<List<Map<StoreDetails, List<Menu>?>>> {
        return storeDao.getBookmarkList().toDomain()
    }

    override suspend fun getStoresByFilter(
        code: List<String>,
        gu: String?,
        bookmarked: Boolean,
        centerX: Double,
        centerY: Double,
        r: Double
    ): List<StoreDetails> {
        return storeDao.getStoresByFilter(code, gu, bookmarked, centerX, centerY, r).toDomain()
    }

}