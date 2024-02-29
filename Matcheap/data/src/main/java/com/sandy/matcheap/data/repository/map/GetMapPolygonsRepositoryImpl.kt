package com.sandy.matcheap.data.repository.map

import com.sandy.matcheap.data.room.dao.MapDao
import com.sandy.matcheap.domain.model.map.Polygon
import com.sandy.matcheap.domain.repository.map.GetMapPolygonsRepository
import javax.inject.Inject

class GetMapPolygonsRepositoryImpl @Inject constructor(
    private val dao: MapDao
): GetMapPolygonsRepository {

    override suspend fun getPolygons(): Map<String, List<Polygon>> {
        return dao.getPolygons()
    }
}