package com.sandy.matcheap.data.utils

import android.content.Context
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
        val d = sqrt(
            (lat - curLat).pow(2.0) + (lng - curLng).pow(2.0)
        )
        return String.format("%.1f km", 100 * d)
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