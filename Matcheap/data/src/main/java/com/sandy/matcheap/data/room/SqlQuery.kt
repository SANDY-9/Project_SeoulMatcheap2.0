package com.sandy.matcheap.data.room

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
