package com.sandy.matcheap.data.utils

import java.text.SimpleDateFormat
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.tan

object ForecastUtil {
    fun getSensoryTemperature(t: Double?, v: Double?): Double  {
        return when {
            t == null || v == null -> 0.0
            v <= 0 -> t
            else -> round((13.12 + 0.6215 * t - 11.37 * v.pow(0.16) + 0.3965 * v.pow(0.16) * t) * 10) / 10
        }
    }

    private val timeFormat = SimpleDateFormat("yyyyMMdd-HHmm")
    private val halfTimeFormat = SimpleDateFormat("yyyyMMdd-HH30")
    private const val ONE_HOUR = 3600000
    fun getBaseTime(): List<String> {
        val time = LocalTime.now()
        val currentTime = System.currentTimeMillis()

        val baseTime = if(time.minute < 30) halfTimeFormat.format(currentTime - ONE_HOUR).split("-")
        else timeFormat.format(currentTime).split("-")

        //date = baseTime[0],
        //time = baseTime[1],
        return baseTime
    }


    private const val DEGRAD = Math.PI / 180.0// 지구 반경(km)
    private const val GRID = 5.0 // 격자 간격(km)
    private const val RE = 6371.00877 / GRID
    private const val SLAT1 = 30.0 * DEGRAD// 투영 위도1(degree)
    private const val SLAT2 = 60.0 * DEGRAD// 투영 위도2(degree)
    private const val OLON = 126.0 * DEGRAD // 기준점 경도(degree)
    private const val OLAT = 38.0 * DEGRAD // 기준점 위도(degree)
    private const val XO = 43.0 // 기준점 X좌표(GRID)
    private const val YO = 136.0 // 기1준점 Y좌표
    private val sn = ln(cos(SLAT1) / cos(SLAT2)) / ln(
        tan(Math.PI * 0.25 + SLAT2 * 0.5) / tan(Math.PI * 0.25 + SLAT1 * 0.5)
    )
    private val sf = (tan(Math.PI * 0.25 + SLAT1 * 0.5)).pow(sn) * cos(SLAT1) / sn
    private val ro = RE * sf / (tan(Math.PI * 0.25 + OLAT * 0.5)).pow(sn)
    fun getConvertedGrid(lat: Double, lng: Double): Pair<Int, Int> {

        val ra = RE * sf / (tan(Math.PI * 0.25 + lat * DEGRAD * 0.5)).pow(sn)
        var theta = lng * DEGRAD - OLON

        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        val nx = floor(ra * sin(theta) + XO + 0.5).toInt()
        val ny = floor(ro - ra * cos(theta) + YO + 0.5).toInt()

        return nx to ny
    }

}