package com.sandy.matcheap.domain.usecases.forecast

import com.sandy.matcheap.common.MESSAGE_NETWORK_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.model.forecast.Forecast
import com.sandy.matcheap.domain.repository.forecast.GetForecastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val getForecastRepository: GetForecastRepository
) {

    operator fun invoke(lat: Double, lng: Double): Flow<Resource<Forecast>> = flow {
        emit(Resource.Loading())
        try {
            val forecast = getForecastRepository.getCurrentForecast(lat, lng)
            emit(Resource.Success(forecast))
        } catch (e: Exception) {
            emit(Resource.Error(MESSAGE_NETWORK_ERROR))
        }
    }

}