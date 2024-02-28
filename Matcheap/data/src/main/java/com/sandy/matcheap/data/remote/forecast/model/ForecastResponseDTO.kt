package com.sandy.matcheap.data.remote.forecast.model


import com.google.gson.annotations.SerializedName

data class ForecastResponseDTO(
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
                val item: List<ForecastDTO>
            )
        }
    }
}
