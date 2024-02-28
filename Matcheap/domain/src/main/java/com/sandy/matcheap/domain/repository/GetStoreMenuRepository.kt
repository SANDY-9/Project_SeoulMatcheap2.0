package com.sandy.matcheap.domain.repository

import com.sandy.matcheap.domain.model.menu.Menu

interface GetStoreMenuRepository {

    suspend fun getMenu(id: String): List<Menu>

}