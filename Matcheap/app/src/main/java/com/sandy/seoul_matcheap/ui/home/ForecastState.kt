package com.sandy.seoul_matcheap.ui.home

import com.sandy.matcheap.domain.model.forecast.Forecast

data class ForecastState(
    val isLoading: Boolean = false,
    val data: Forecast? = null,
    val error: String = ""
)
