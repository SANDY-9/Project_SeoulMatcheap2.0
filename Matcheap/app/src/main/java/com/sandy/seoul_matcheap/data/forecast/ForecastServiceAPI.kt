package com.sandy.seoul_matcheap.data.forecast

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-07
 * @desc
 */
interface ForecastServiceAPI {

    @GET("getUltraSrtFcst")
    suspend fun getTodayForecast(
        @Query("base_date") date: String,
        @Query("base_time") time: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int,
        @Query("dataType") dataType : String = DATA_TYPE,
        @Query("numOfRows") numOfRows : Int = NUM_OF_ROWS
    ) : Response<ForecastResponse>

    private companion object {
        private const val DATA_TYPE = "json"
        private const val NUM_OF_ROWS = 1000
    }
}