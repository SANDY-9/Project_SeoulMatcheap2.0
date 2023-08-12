package com.sandy.seoul_matcheap.data.store.repository

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
    suspend fun downloadSurroundingStores() = dao.getSurroundingStores()

    suspend fun downloadRandomStores() = dao.getRandomStores()

    fun downloadStoreList(gu: String? = null) = dao.run {
        if(gu == null) getStoreList()
        else getStoreList(gu)
    }


    // !-- select storeCount
    suspend fun downloadStoreCount(code: String, gu: String? = null) = dao.run {
        when(code) {
            DEFAULT_ -> {
                if(gu == null) getStoreCountByDistance()
                else getStoreCountByGu(gu)
            }
            else -> {
                if(gu == null) getStoreCountByDistanceAndCode(code)
                else getStoreCountByGuAndCode(gu, code)
            }
        }
    }

    suspend fun downloadStoreTotalCountForCode() = dao.getStoreTotalCountForCode()

    suspend fun downloadStoreTotalCount() = dao.getStoreTotalCount()

}