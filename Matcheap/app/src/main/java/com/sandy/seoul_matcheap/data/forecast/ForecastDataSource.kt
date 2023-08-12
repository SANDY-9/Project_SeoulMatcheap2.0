package com.sandy.seoul_matcheap.data.forecast

import com.sandy.seoul_matcheap.util.helper.DataHelper
import javax.inject.Inject
import java.lang.Exception

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-07
 * @desc
 */
class ForecastDataSource @Inject constructor(private val api: ForecastServiceAPI) {

    suspend fun fetchCurrentForecast(lat: Double, lng:Double) : List<Forecast> {
        var result = listOf<Forecast>()
        try {
            val date = DataHelper.getBaseTime()
            val grid = DataHelper.convertToGrid(lat, lng)
            api.getTodayForecast(
                date = date[0],
                time = date[1],
                nx = grid.first,
                ny = grid.second
            ).body()?.let {
                result = filterTodayForecastData(it)
            }
        } catch (e: Exception) { }
        return result
    }

    private fun filterTodayForecastData(forecastResponse: ForecastResponse) : List<Forecast> {
        val baseTime = DataHelper.getBaseTime()[1].slice(0..1).toInt()
        val fcstTime = if(baseTime + 1 == 24) "0000" else "${String.format("%02d", baseTime + 1)}00"
        return forecastResponse.response.body.items.item.filter {
            it.fcstTime == fcstTime
        }
    }

}