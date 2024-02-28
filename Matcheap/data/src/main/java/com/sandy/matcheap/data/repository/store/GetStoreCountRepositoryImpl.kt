package com.sandy.matcheap.data.repository.store

import com.sandy.matcheap.common.DEFAULT_
import com.sandy.matcheap.data.mappers.toDomain
import com.sandy.matcheap.data.mappers.toInt
import com.sandy.matcheap.data.room.dao.CountDao
import com.sandy.matcheap.domain.repository.store.GetStoreCountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoreCountRepositoryImpl @Inject constructor(
    private val dao: CountDao
): GetStoreCountRepository {

    override suspend fun getStoreCount(
        code: String,
        curLat: Double,
        curLng: Double,
        r: Double
    ): Int {
        return when(code) {
            DEFAULT_ -> dao.getStoreCountByDistance(curLat, curLng, r).toInt()
            else -> dao.getStoreCountByDistanceAndCode(curLat, curLng, r, code).toInt()
        }
    }

    override suspend fun getStoreCount(gu: String, code: String): Int {
        return when(code) {
            DEFAULT_ -> dao.getStoreCountByGu(gu)
            else -> dao.getStoreCountByGuAndCode(gu, code)
        }
    }

    override suspend fun getStoreTotalCountForCode(): Map<String, Int> {
        return dao.getStoreTotalCountForCode().toDomain()
    }

    override suspend fun getStoreTotalCount(): Int {
        return dao.getStoreTotalCount()
    }

    override fun getBookmarkedStoreCount(code: String): Flow<Int> {
        return if(code == DEFAULT_) dao.getBookmarkedStoreCount() else dao.getBookmarkedStoreCountByCode(code)
    }

    override suspend fun getStoreCountByFilter(
        code: List<String>,
        gu: String?,
        bookmarked: Boolean,
        centerX: Double,
        centerY: Double,
        r: Double?
    ): Map<String, Int> {
        return dao.getStoreCountByFilter(code, gu, bookmarked, centerX, centerY, r)
    }
}