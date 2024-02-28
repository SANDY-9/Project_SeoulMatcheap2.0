package com.sandy.matcheap.domain.repository

import com.sandy.matcheap.domain.model.forecast.Forecast

interface GetForecastRepository {
    suspend fun getCurrentForecast(lat: Double, lng:Double) : Forecast

}