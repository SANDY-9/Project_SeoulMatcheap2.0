package com.sandy.matcheap.domain.usecases.menu

import com.sandy.matcheap.common.MESSAGE_NETWORK_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.model.menu.Menu
import com.sandy.matcheap.domain.repository.menu.GetStoreMenuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStoreMenuUseCase @Inject constructor(
    private val getStoreMenuRepository: GetStoreMenuRepository
) {

    operator fun invoke(id: String): Flow<Resource<List<Menu>>> = flow {
        emit(Resource.Loading())
        try {
            val menus = getStoreMenuRepository.getMenu(id)
            emit(Resource.Success(menus))
        } catch (e: Exception) {
            emit(Resource.Error(MESSAGE_NETWORK_ERROR))
        }
    }

}