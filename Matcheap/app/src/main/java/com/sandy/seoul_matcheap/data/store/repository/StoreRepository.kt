package com.sandy.seoul_matcheap.data.store.repository

import android.location.Location
import com.sandy.seoul_matcheap.data.store.dao.*
import com.sandy.seoul_matcheap.data.store.entity.StoreInfo
import com.sandy.seoul_matcheap.util.constants.DEFAULT_
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-03
 * @desc
 */
class StoreRepository @Inject constructor(private val dao: StoreDao) {

    suspend fun insertStores(stores: List<StoreInfo>) = dao.insertStores(stores)

    suspend fun downloadStoreDetails(id: String) = dao.getStoreDetails(id)

    suspend fun downloadRecommendStore() = dao.getRecommendStore()


    //!-- select storeList
    suspend fun downloadSurroundingStores(location: Location) = dao.getSurroundingStores(
        location.latitude, location.longitude
    )

    suspend fun downloadRandomStores() = dao.getRandomStores()

    fun downloadStoreList(curLat: Double, curLng: Double, gu: String) = dao.getStoreListByRegion(curLat, curLng, gu)
    fun downloadStoreList(curLat: Double, curLng: Double, r: Double) = dao.getStoreListByCategory(curLat, curLng, r)


    // !-- select storeCount
    suspend fun downloadStoreCount(code: String, curLat: Double, curLng: Double, r: Double) = when(code) {
        DEFAULT_ -> dao.getStoreCountByDistance(curLat, curLng, r)
        else -> dao.getStoreCountByDistanceAndCode(curLat, curLng, r, code)
    }

    suspend fun downloadStoreCount(gu: String, code: String) = when(code) {
        DEFAULT_ -> dao.getStoreCountByGu(gu)
        else -> dao.getStoreCountByGuAndCode(gu, code)
    }

    suspend fun downloadStoreTotalCountForCode() = dao.getStoreTotalCountForCode()

    suspend fun downloadStoreTotalCount() = dao.getStoreTotalCount()

}