package com.sandy.matcheap.data.mappers

import com.sandy.matcheap.data.remote.forecast.model.ForecastDTO
import com.sandy.matcheap.data.utils.ForecastUtil
import com.sandy.matcheap.domain.model.forecast.Forecast

fun List<ForecastDTO>.toDomain(): Forecast {

    var t1H: String? = null
    var wsd: String? = null
    var pty: String? = null
    var sky: String? = null

    val baseTime = ForecastUtil.getBaseTime()[1].slice(0..1).toInt()
    val fcstTime = if(baseTime + 1 == 24) "0000" else "${String.format("%02d", baseTime + 1)}00"

    forEach {
        //baseTime에 대한 결과만 가져오기
        if(it.fcstTime == fcstTime) {
            //filtering된 결과 중에 t1H, wsd, pty, sky 뽑아내기
            when (it.category?.trim()) {
                T1H -> t1H = it.fcstValue
                PTY -> pty = it.fcstValue
                SKY -> sky = it.fcstValue
                WSD -> wsd = it.fcstValue
            }
        }

    }

    return Forecast(
        temperature = t1H?: "",
        sensoryTemperature = ForecastUtil.getSensoryTemperature(t1H?.toDouble(), wsd?.toDouble()),
        pty = pty ?: "",
        sky = sky ?: ""
    )
}



private const val T1H = "T1H"
private const val SKY = "SKY"
private const val PTY = "PTY"
private const val WSD = "WSD"