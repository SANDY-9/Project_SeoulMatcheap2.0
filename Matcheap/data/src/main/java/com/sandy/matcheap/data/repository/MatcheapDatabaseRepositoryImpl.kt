package com.sandy.matcheap.data.repository

import com.sandy.matcheap.data.room.dao.MapDao
import com.sandy.matcheap.data.room.dao.StoreDao
import com.sandy.matcheap.domain.model.map.Polygon
import com.sandy.matcheap.domain.model.store.StoreDetails
import com.sandy.matcheap.domain.repository.MatcheapDatabaseRepository
import javax.inject.Inject

class MatcheapDatabaseRepositoryImpl @Inject constructor(
    private val storeDao: StoreDao,
    private val mapDao: MapDao
): MatcheapDatabaseRepository {
    override suspend fun insertStores(stores: List<StoreDetails>) {
        storeDao.insertStores(stores)
    }

    override suspend fun insertPolygons(polygons: List<Polygon>) {
        mapDao.insertPolygons(polygons)
    }

}