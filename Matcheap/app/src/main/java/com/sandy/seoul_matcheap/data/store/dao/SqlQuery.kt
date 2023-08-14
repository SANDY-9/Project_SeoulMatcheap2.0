package com.sandy.seoul_matcheap.data.store.dao

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-08-14
 * @desc
 */

const val DEFAULT_DISTANCE = 5
const val CURRENT_LOCATION_QUERY =":curLat AS curLat, :curLng AS curLng"
const val DISTANCE_QUERY = "ABS(lat - :curLat) + ABS(lng - :curLng) AS distance"