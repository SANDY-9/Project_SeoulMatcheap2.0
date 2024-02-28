package com.sandy.matcheap.domain.model.forecast

data class Forecast(
    val temperature: String, //기온
    val sensoryTemperature: Double, //체감온도
    val pty: String, //강수 상태
    val sky: String //구름 상태
)
