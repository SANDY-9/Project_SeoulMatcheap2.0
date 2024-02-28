package com.sandy.matcheap.domain.repository.store

import kotlinx.coroutines.flow.Flow

interface GetStoreCountRepository {

    suspend fun getStoreCount(code: String, curLat: Double, curLng: Double, r: Double): Int
    suspend fun getStoreCount(gu: String, code: String): Int
    suspend fun getStoreTotalCountForCode(): Map<String, Int>
    suspend fun getStoreTotalCount(): Int
    fun getBookmarkedStoreCount(code: String): Flow<Int>
    suspend fun getStoreCountByFilter(
        code: List<String>,
        gu: String?,
        bookmarked: Boolean,
        centerX: Double,
        centerY: Double,
        r: Double?
    ): Map<String, Int>

}