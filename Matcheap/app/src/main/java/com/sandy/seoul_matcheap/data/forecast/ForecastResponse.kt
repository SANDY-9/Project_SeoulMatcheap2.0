package com.sandy.seoul_matcheap.data.forecast


import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("response")
    val response: Response
) {
    data class Response(
        @SerializedName("body")
        val body: Body
    ) {
        data class Body(
            @SerializedName("dataType")
            val dataType: String, // JSON
            @SerializedName("items")
            val items: Items,
            @SerializedName("numOfRows")
            val numOfRows: Int, // 1000
            @SerializedName("pageNo")
            val pageNo: Int, // 1
            @SerializedName("totalCount")
            val totalCount: Int // 60
        ) {
            data class Items(
                @SerializedName("item")
                val item: List<Forecast>
            )
        }
    }
}

data class Forecast(
    @SerializedName("baseDate")
    val baseDate: String, // 20230309
    @SerializedName("baseTime")
    val baseTime: String, // 2330
    @SerializedName("category")
    val category: String, // LGT
    @SerializedName("fcstDate")
    val fcstDate: String, // 20230310
    @SerializedName("fcstTime")
    val fcstTime: String, // 0000
    @SerializedName("fcstValue")
    val fcstValue: String, // 0
    @SerializedName("nx")
    val nx: Int, // 55
    @SerializedName("ny")
    val ny: Int // 127
)
