package com.sandy.matcheap.data.remote.forecast.model

import com.google.gson.annotations.SerializedName

data class ForecastDTO(
    @SerializedName("baseDate")
    val baseDate: String, // 20230309
    @SerializedName("baseTime")
    val baseTime: String, // 2330
    @SerializedName("category")
    val category: String?, // LGT
    @SerializedName("fcstDate")
    val fcstDate: String, // 20230310
    @SerializedName("fcstTime")
    val fcstTime: String, // 0000
    @SerializedName("fcstValue")
    val fcstValue: String?, // 0
    @SerializedName("nx")
    val nx: Int, // 55
    @SerializedName("ny")
    val ny: Int // 127
)
