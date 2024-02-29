package com.sandy.matcheap.domain.appdb.repository

import com.sandy.matcheap.domain.model.map.Polygon
import com.sandy.matcheap.domain.model.store.StoreDetails

interface MatcheapDatabaseRepository {

    suspend fun insertStores(stores: List<StoreDetails>)
    suspend fun insertPolygons(polygons: List<Polygon>)

}