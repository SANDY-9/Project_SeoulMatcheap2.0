package com.sandy.matcheap.data.room.dao

import androidx.room.*
import com.sandy.matcheap.domain.model.map.Polygon

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-02
 * @desc
 */
@Dao
interface MapDao {

    // !-- polygon
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolygons(polygons: List<Polygon>)

    @Query("SELECT gu, num, lat, lng " +
                "FROM map_polygon " +
                "GROUP BY gu, num")
    @MapInfo(keyColumn = "gu", valueTable = "map_polygon")
    suspend fun getPolygons() : Map<String, List<Polygon>>

}