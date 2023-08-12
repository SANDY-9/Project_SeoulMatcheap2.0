package com.sandy.seoul_matcheap.data.store

import com.sandy.seoul_matcheap.data.store.entity.*
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-27
 * @desc
 */


class SeoulOpenAPIDataSource @Inject constructor(private val api: SeoulOpenAPI) {

    suspend fun fetchMenu(id: String) : List<Menu>? {
        return try {
            val result = api.getMenu(id).body()
            result!!.listPriceModelStoreProductService.result
        } catch (e: Exception) {
            null
        }
    }

}