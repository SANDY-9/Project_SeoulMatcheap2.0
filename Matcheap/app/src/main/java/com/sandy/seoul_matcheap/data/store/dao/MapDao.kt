package com.sandy.seoul_matcheap.data.store.dao

import androidx.room.*
import com.sandy.seoul_matcheap.data.store.entity.Polygon
import java.io.Serializable

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


    // !-- store
    @Transaction
    @Query(
        "SELECT filtered_stores.id, filtered_stores.code, gu, name, address, " +
                "photo, lat, lng, d, " +
                "COALESCE(bookmarked, false) AS bookmarked " +
                "FROM (" +
                    "SELECT id, code, gu, name, address, photo, lat, lng, $MAP_CENTER_LOCATION_QUERY, $MAP_DISTANCE_QUERY " +
                    "FROM store_info " +
                    "WHERE d <= :r " +
                    "AND code IN (:code) " +
                    "AND (:gu IS NULL OR gu = :gu) " +
                ") AS filtered_stores " +
                "LEFT JOIN store_bookmark " +
                "ON filtered_stores.id = store_bookmark.id " +
                "WHERE (:bookmarked = false OR bookmarked = :bookmarked)"
    )
    suspend fun getStoresByFilter(
        code: List<String>,
        gu: String?,
        bookmarked: Boolean,
        centerX: Double,
        centerY: Double,
        r: Double
    ) : List<StoreMapItem>

    @Query(
        "SELECT gu, count(*) AS total " +
                "FROM (" +
                    "SELECT filtered_stores.id, filtered_stores.code, gu, name, address, " +
                    "photo, lat, lng, d, " +
                    "COALESCE(bookmarked, false) AS bookmarked " +
                        "FROM (" +
                            "SELECT id, code, gu, name, address, photo, lat, lng, $MAP_CENTER_LOCATION_QUERY, $MAP_DISTANCE_QUERY " +
                            "FROM store_info " +
                            "WHERE (:r IS NULL OR d <= :r) " +
                            "AND code IN (:code) " +
                            "AND (:gu IS NULL OR gu = :gu) " +
                        ") AS filtered_stores " +
                        "LEFT JOIN store_bookmark " +
                        "ON filtered_stores.id = store_bookmark.id " +
                        "WHERE (:bookmarked = false OR bookmarked = :bookmarked)" +
                ")" +
                "GROUP BY gu "
    )
    @MapInfo(keyColumn = "gu", valueColumn = "total")
    suspend fun getStoreCountByGu(
        code: List<String>,
        gu: String?,
        bookmarked: Boolean,
        centerX: Double,
        centerY: Double,
        r: Double?
    ) : Map<String, Int>

}

data class StoreMapItem(
    val id: String,
    val code: String,
    val gu: String,
    val	name: String,
    val	address: String,
    val	photo: String,
    val	lat: Double,
    val	lng: Double,
    val d: Float,
    var bookmarked: Boolean = false
) : Serializable
