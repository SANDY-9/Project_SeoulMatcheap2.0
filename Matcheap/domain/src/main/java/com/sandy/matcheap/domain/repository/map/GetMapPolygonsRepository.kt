package com.sandy.matcheap.domain.repository.map

import com.sandy.matcheap.domain.model.map.Polygon

interface GetMapPolygonsRepository {

    suspend fun getPolygons(): Map<String, List<Polygon>>

}