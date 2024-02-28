package com.sandy.matcheap.domain.repository.forecast

import com.sandy.matcheap.domain.model.forecast.Forecast

interface GetForecastRepository {
    suspend fun getCurrentForecast(lat: Double, lng:Double) : Forecast

}