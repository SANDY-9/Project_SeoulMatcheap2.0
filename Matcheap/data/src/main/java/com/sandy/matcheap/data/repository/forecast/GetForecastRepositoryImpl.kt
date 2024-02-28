package com.sandy.matcheap.data.repository.forecast

import com.sandy.matcheap.data.mappers.toDomain
import com.sandy.matcheap.data.remote.forecast.ForecastServiceAPI
import com.sandy.matcheap.data.utils.ForecastUtil
import com.sandy.matcheap.domain.model.forecast.Forecast
import com.sandy.matcheap.domain.repository.forecast.GetForecastRepository
import javax.inject.Inject

class GetForecastRepositoryImpl @Inject constructor(
    private val api: ForecastServiceAPI
): GetForecastRepository {

    override suspend fun getCurrentForecast(lat: Double, lng: Double): Forecast {

        val baseTime = ForecastUtil.getBaseTime()
        val grid = ForecastUtil.getConvertedGrid(lat, lng)

        val response = api.getTodayForecast(
            date = baseTime[0],
            time = baseTime[1],
            nx = grid.first,
            ny = grid.second
        ).body()?.response?.body?.items?.item

        return response?.toDomain() ?: Forecast(
            temperature = "",
            sensoryTemperature = 0.0,
            pty = "",
            sky = ""
        )

    }

}