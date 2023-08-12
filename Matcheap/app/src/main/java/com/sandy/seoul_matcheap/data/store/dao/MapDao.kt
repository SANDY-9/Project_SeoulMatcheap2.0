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

    @Query("SELECT * FROM map_polygon ORDER BY gu")
    suspend fun getPolygons() : List<Polygon>


    // !-- store
    @Transaction
    @Query(
        "SELECT store_info.id, store_info.code, codeName, gu, name, address, " +
                "photo, lat, lng, distance, COALESCE(bookmarked, false) AS bookmarked " +
                "FROM store_info " +
                "LEFT JOIN store_bookmark  " +
                "ON store_info.id = store_bookmark.id " +
                "ORDER BY distance"
    )
    suspend fun getStores() : List<StoreMapItem>

    @Query(
        "SELECT gu, count(*) " +
                "FROM store_info " +
                "GROUP BY gu " +
                "ORDER BY gu"
    )
    suspend fun getStoreCountForGu() : List<StoreCountForGu>

}

data class StoreCountForGu(
    @ColumnInfo(name = "gu") val gu: String,
    @ColumnInfo(name = "count(*)") val count: Int
)

data class StoreMapItem(
    val id: String,
    val code: String,
    val codeName: String,
    val gu: String,
    val	name: String,
    val	address: String,
    val	photo: String,
    val	lat: Double,
    val	lng: Double,
    val distance: Double,
    var bookmarked: Boolean = false
) : Serializable