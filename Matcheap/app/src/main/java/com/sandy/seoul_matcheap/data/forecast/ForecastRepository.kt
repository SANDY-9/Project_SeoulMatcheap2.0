package com.sandy.seoul_matcheap.data.forecast

import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-09
 * @desc
 */
class ForecastRepository @Inject constructor(private val forecastDataSource: ForecastDataSource) {
    suspend fun downloadCurrentForecast(lat: Double, lng:Double) =
        forecastDataSource.fetchCurrentForecast(lat, lng)

}