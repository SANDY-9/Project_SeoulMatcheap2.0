package com.sandy.seoul_matcheap.util.helper

import android.content.Context
import android.location.Location
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import java.util.regex.Pattern
import kotlin.math.*

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-05
 * @desc
 */
object DataHelper {

    fun downloadStoreData(context: Context) = context.resources.assets
        .open("store.txt")
        .bufferedReader()
        .readLines()

    fun downloadPolygonData(context: Context) = context.resources.assets
        .open("polygon.txt")
        .bufferedReader()
        .readLines()

    fun convertDistance(lat: Double, lng : Double, curLat: Double, curLng: Double) : String {
        val a = 2 * asin(
            sqrt(
                sin(Math.toRadians(lat - curLat) / 2).pow(2.0)
                        + sin(Math.toRadians(lng - curLng) / 2).pow(2.0)
                        * cos(Math.toRadians(curLat))
                        * cos(Math.toRadians(lat))
            )
        )
        return String.format("%.1f km", 6372.8 * a)
    }

    private val timeFormat = SimpleDateFormat("yyyyMMdd-HHmm")
    private val halfTimeFormat = SimpleDateFormat("yyyyMMdd-HH30")
    private const val ONE_HOUR = 3600000
    fun getBaseTime() = run {
        val time = LocalTime.now()
        val currentTime = System.currentTimeMillis()

        if(time.minute < 30) halfTimeFormat.format(currentTime - ONE_HOUR).split("-")
        else timeFormat.format(currentTime).split("-")
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd ")
    fun getDate() : String {
        val date = LocalDate.now()
        return date.format(dateFormatter) + date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREA)
    }

    private val formatter = DateTimeFormatter.ofPattern("yyyy.M.d.")
    private val convertFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    fun convertDateFormat(date: String) : String {
        return try {
            val localDate = LocalDate.parse(date.replace(" ", ""), formatter)
            localDate.format(convertFormatter)
        } catch (e: Exception) {
            date
        }
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
    fun convertToGrid(lat: Double, lng: Double) = run {

        val ra = RE * sf / (tan(Math.PI * 0.25 + lat * DEGRAD * 0.5)).pow(sn)
        var theta = lng * DEGRAD - OLON

        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        val nx = floor(ra * sin(theta) + XO + 0.5).toInt()
        val ny = floor(ro - ra * cos(theta) + YO + 0.5).toInt()

        nx to ny
    }

    fun calculateSensoryTemperature(t: Double, v: Double) =
        if(v <= 0) t else round((13.12 + 0.6215 * t - 11.37 * v.pow(0.16) + 0.3965 * v.pow(0.16) * t) * 10) / 10


    private const val TIME_24 = 86400
    fun calculateDuration(hour: Int, min: Int): Long {
        val duration = Duration.between(
            LocalTime.now(),
            LocalTime.of(hour, min, 0)
        ).seconds
        return if(duration < 0) duration + TIME_24 else duration
    }


    //!-- Autocomplete settings
    fun removeSpace(str: String) = str.trim().replace(" ", "").trim()
    fun getStartIndex(
        input: String,
        auto: String,
        removedSpaceInput: String,
        removedSpaceAuto: String
    ) = auto.run {
        when {
            contains(input) -> indexOf(input)
            contains(removedSpaceInput) -> indexOf(removedSpaceInput)
            else -> getOriginalIndex(
                auto,
                removedSpaceAuto,
                removedSpaceAuto.indexOf(removedSpaceInput)
            )
        }
    }
    fun getEndIndex(
        input: String,
        auto: String,
        removedSpaceInput: String,
        removedSpaceAuto: String,
        startIndex: Int
    ) = auto.run {
        when {
            contains(input) -> startIndex + input.length
            contains(removedSpaceInput) -> startIndex + removedSpaceInput.length
            else -> getOriginalIndex(
                auto,
                removedSpaceAuto,
                removedSpaceAuto.indexOf(removedSpaceInput) + removedSpaceInput.length - 1
            ) + 1
        }
    }
    private fun getOriginalIndex(auto: String, removedSpaceAuto: String, index: Int): Int {
        val targetChar = removedSpaceAuto[index]
        val count = auto.count { it == targetChar }

        // if multiple targetChar exist, original index is the smallest index among them that is more than index.
        // because length of targetChar is 1, have used Pattern, Matcher Class(Regular Expression).
        val originalIndex = if(count > 1) {
            val matcher = Pattern.compile(targetChar.toString()).matcher(auto)
            mutableListOf<Int>().apply {
                while (matcher.find()) {
                    if(matcher.start() > index) {
                        add(matcher.start())
                        break
                    }
                }
            }.first()
        } else auto.indexOf(targetChar)

        val spaceCount = auto.substring(0, originalIndex).count { it.isWhitespace() }
        return index + spaceCount
    }

}