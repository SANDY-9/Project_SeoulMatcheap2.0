package com.sandy.matcheap.domain.usecases.map

import com.sandy.matcheap.common.MESSAGE_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.model.map.Polygon
import com.sandy.matcheap.domain.repository.map.GetMapPolygonsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMapPolygonsUseCase @Inject constructor(
    private val getMapPolygonsRepository: GetMapPolygonsRepository
) {

    operator fun invoke(): Flow<Resource<Map<String, List<Polygon>>>> = flow {
        emit(Resource.Loading())
        try {
            val stores = getMapPolygonsRepository.getPolygons()
            emit(Resource.Success(stores))
        } catch (e: Exception) {
            emit(Resource.Error(MESSAGE_ERROR))
        }
    }
}