package com.sandy.seoul_matcheap.data.store.repository

import com.sandy.seoul_matcheap.data.store.dao.MapDao
import com.sandy.seoul_matcheap.data.store.entity.Polygon
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-03
 * @desc
 */
class MapRepository @Inject constructor(private val dao: MapDao) {

    suspend fun insertPolygons(polygons: List<Polygon>) = dao.insertPolygons(polygons)
    suspend fun downloadPolygons() = dao.getPolygons()

    suspend fun downloadStoresByFilter(
        code: List<String>,
        gu: String?,
        bookmarked: Boolean,
        centerX: Double,
        centerY: Double,
        r: Double
    ) = dao.getStoresByFilter(
        code, gu, bookmarked, centerX, centerY, r
    )

    suspend fun downloadStoreCountByGu(
        code: List<String>,
        gu: String?,
        bookmarked: Boolean,
        centerX: Double,
        centerY: Double,
        r: Double?
    ) = dao.getStoreCountByGu(
        code, gu, bookmarked, centerX, centerY, r
    )

}