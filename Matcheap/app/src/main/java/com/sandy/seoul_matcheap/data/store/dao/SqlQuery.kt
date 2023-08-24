package com.sandy.seoul_matcheap.data.store.dao

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-08-14
 * @desc
 */

const val CURRENT_LOCATION_QUERY =":curLat AS curLat, :curLng AS curLng"
const val DISTANCE_QUERY = "(lat - :curLat)*(lat - :curLat) + (lng - :curLng)*(lng - :curLng) AS d"
const val MAP_CENTER_LOCATION_QUERY =":centerX AS centerX, :centerY AS centerY"
const val MAP_DISTANCE_QUERY = "(lat - :centerX)*(lat - :centerX) + (lng - :centerY)*(lng - :centerY) AS d"

sealed class DistanceRadius(val value: Double) {
    object M500 : DistanceRadius(0.0055 * 0.0055)
    object M1000 : DistanceRadius(0.01 * 0.01)
    object M2000 : DistanceRadius(0.02 * 0.02)
    object M2500 : DistanceRadius(0.025 * 0.025)
    object M3000 : DistanceRadius(0.03 * 0.03)
}