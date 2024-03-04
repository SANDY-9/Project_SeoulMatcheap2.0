package com.sandy.matcheap.data.room.dao

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import com.sandy.matcheap.data.room.DISTANCE_QUERY
import com.sandy.matcheap.data.room.MAP_CENTER_LOCATION_QUERY
import com.sandy.matcheap.data.room.MAP_DISTANCE_QUERY
import com.sandy.matcheap.data.room.dto.CountDTO
import com.sandy.matcheap.data.room.dto.StoreCountDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface CountDao {

    // 거리 조건에 따라 착한가격업소 개수 반환
    @Query(
        "SELECT count(*), lat, lng, $DISTANCE_QUERY " +
                "FROM store_info " +
                "WHERE d <= :r "
    )
    suspend fun getStoreCountByDistance(curLat: Double, curLng: Double, r: Double) : CountDTO

    // 거리와 카테고리 조건에 따라 착한가격업소 개수 반환
    @Query(
        "SELECT count(*), lat, lng, $DISTANCE_QUERY " +
                "FROM store_info " +
                "WHERE d <= :r " +
                "AND code = :code"
    )
    suspend fun getStoreCountByDistanceAndCode(curLat: Double, curLng: Double, r: Double, code: String) : CountDTO

    // 지역 조건에 따라 착한가격업소 개수 반환
    @Query(
        "SELECT count(*) " +
                "FROM store_info " +
                "WHERE gu = :gu "
    )
    suspend fun getStoreCountByGu(gu: String) : Int

    // 지역과 카데고리 조건에 따라 착한가격업소 개수 반환
    @Query(
        "SELECT count(*) " +
                "FROM store_info " +
                "WHERE gu = :gu " +
                "AND code = :code "
    )
    suspend fun getStoreCountByGuAndCode(gu: String, code: String) : Int

    // 카테고리별 착한가격업소 개수 반환 (카테고리, 개수)
    @Query(
        "SELECT code, count(*) " +
                "FROM store_info " +
                "GROUP BY code " +
                "ORDER BY code "
    )
    suspend fun getStoreTotalCountForCode() : List<StoreCountDTO>

    // 총(total) 착한가격업소 데이터 개수 반환
    @Query("SELECT count(*) FROM store_info ")
    suspend fun getStoreTotalCount() : Int

    // 모든 조건에 따라 착한가격업소 개수 반환
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
    suspend fun getStoreCountByFilter(
        code: List<String>,
        gu: String?,
        bookmarked: Boolean,
        centerX: Double,
        centerY: Double,
        r: Double?
    ) : Map<String, Int>

}