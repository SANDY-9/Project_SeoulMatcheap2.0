package com.sandy.matcheap.domain.usecases.store

import androidx.paging.PagingSource
import com.sandy.matcheap.common.DistanceRadius
import com.sandy.matcheap.common.MESSAGE_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.model.menu.Menu
import com.sandy.matcheap.domain.model.store.StoreDetails
import com.sandy.matcheap.domain.model.store.StoreItem
import com.sandy.matcheap.domain.repository.store.GetStoreListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStoreListUseCase @Inject constructor(
    private val getStoreListRepository: GetStoreListRepository
) {
    fun getSurroundingStores(curLat: Double, curLng: Double): Flow<Resource<List<StoreItem>>> = flow {
        emit(Resource.Loading())
        try {
            val stores = getStoreListRepository.getSurroundingStoreList(curLat, curLng)
            emit(Resource.Success(stores))
        } catch (e: Exception) {
            emit(Resource.Error(MESSAGE_ERROR))
        }
    }

    suspend fun getStoreListByGu(
        curLat: Double,
        curLng: Double,
        gu: String
    ): PagingSource<Int, StoreItem> = getStoreListRepository.getStoreListByGu(curLat, curLng, gu)

    suspend fun getStoreListByCategory(
        curLat: Double,
        curLng: Double,
        r: Double = DistanceRadius.M3000.value
    ): PagingSource<Int, StoreItem> = getStoreListRepository.getStoreListByCategory(curLat, curLng, r)

    fun getRandomStores(): Flow<Resource<List<StoreDetails>>> = flow {
        emit(Resource.Loading())
        try {
            val stores = getStoreListRepository.getRandomStoreList()
            emit(Resource.Success(stores))
        } catch (e: Exception) {
            emit(Resource.Error(MESSAGE_ERROR))
        }
    }

    fun getStoresByFilter(
        code: List<String>,
        gu: String?,
        bookmarked: Boolean,
        centerX: Double,
        centerY: Double,
        r: Double
    ) = flow<Resource<List<StoreDetails>>> {
        emit(Resource.Loading())
        try {
            val stores = getStoreListRepository.getStoresByFilter(code, gu, bookmarked, centerX, centerY, r)
            emit(Resource.Success(stores))
        } catch (e: Exception) {
            emit(Resource.Error(MESSAGE_ERROR))
        }
    }

    suspend fun getBookmarkStores(): Flow<List<Map<StoreDetails, List<Menu>?>>> = getStoreListRepository.getBookmarkStoreList()

}