package com.sandy.matcheap.data.repository.menu

import com.sandy.matcheap.data.mappers.toDomain
import com.sandy.matcheap.data.remote.menu.SeoulOpenAPI
import com.sandy.matcheap.domain.model.menu.Menu
import com.sandy.matcheap.domain.repository.menu.GetStoreMenuRepository
import javax.inject.Inject

class GetStoreMenuRepositoryImpl @Inject constructor(
    private val api: SeoulOpenAPI
): GetStoreMenuRepository {

    override suspend fun getMenu(id: String): List<Menu> {
        val response = api.getMenu(id).body()?.listPriceModelStoreProductService?.result
        return response?.toDomain() ?: emptyList()
    }

}