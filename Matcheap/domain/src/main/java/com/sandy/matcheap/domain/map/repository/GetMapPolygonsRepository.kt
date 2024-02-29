package com.sandy.matcheap.domain.map.repository

import com.sandy.matcheap.domain.model.map.Polygon

interface GetMapPolygonsRepository {

    suspend fun getPolygons(): Map<String, List<Polygon>>

}