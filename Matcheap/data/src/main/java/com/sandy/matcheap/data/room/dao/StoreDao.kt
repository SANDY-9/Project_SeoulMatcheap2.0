package com.sandy.matcheap.data.room.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.sandy.matcheap.data.room.CURRENT_LOCATION_QUERY
import com.sandy.matcheap.data.room.DISTANCE_QUERY
import com.sandy.matcheap.data.room.MAP_CENTER_LOCATION_QUERY
import com.sandy.matcheap.data.room.MAP_DISTANCE_QUERY
import com.sandy.matcheap.data.room.dto.RandomStoreDTO
import com.sandy.matcheap.data.room.dto.RecommendStoreDTO
import com.sandy.matcheap.data.room.dto.StoreDetailsDTO
import com.sandy.matcheap.data.room.dto.StoreMapItemDTO
import com.sandy.matcheap.domain.model.store.StoreDetails
import com.sandy.matcheap.domain.model.store.StoreItem
/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-02
 * @desc
 */

@Dao
interface StoreDao {

    // !-- insert data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStores(stores: List<StoreDetails>)


    //착한가격업소 상세정보 불러오기
    @Transaction
    @Query("SELECT * FROM store_info WHERE id = :id")
    suspend fun getStoreDetails(id: String) : StoreDetailsDTO

    //추천 착한가격업소 목록 불러오기 ***
    @Query(
        "SELECT id, name FROM store_info " +
                "WHERE code IN (1, 2, 3, 4) " +
                "ORDER BY RANDOM() "
    )
    suspend fun getRecommendStore() : RecommendStoreDTO

    //주변 착한가격업소 목록 불러오기
    @Query(
        "SELECT id, code, name, address, photo, lat, lng, $CURRENT_LOCATION_QUERY, $DISTANCE_QUERY " +
                "FROM store_info " +
                "ORDER BY d " +
                "LIMIT 5"
    )
    suspend fun getSurroundingStores(curLat: Double, curLng: Double): List<StoreItem>

    //랜덤 착한가격업소 목록 불러오기
    @Query(
        "SELECT id, code, name, address, content, photo, lat, lng " +
                "FROM store_info " +
                "WHERE photo != 'http://tearstop.seoul.go.kr/mulga/photo/' " +
                "ORDER BY RANDOM() " +
                "LIMIT 2"
    )
    suspend fun getRandomStores() : List<RandomStoreDTO>

    //우리동네 착한가격업소 목록 불러오기
    @Query(
        "SELECT id, code, name, address, photo, lat, lng, $CURRENT_LOCATION_QUERY, $DISTANCE_QUERY " +
                "FROM store_info " +
                "WHERE d <= :r " +
                "ORDER BY d "
    )
    fun getStoreListByCategory(
        curLat: Double,
        curLng: Double,
        r: Double
    ) : PagingSource<Int, StoreItem>

    //지역별 착한가격업소 목록 불러오기
    @Query(
        "SELECT id, code, name, address, photo, lat, lng, $CURRENT_LOCATION_QUERY, $DISTANCE_QUERY " +
                "FROM store_info " +
                "WHERE gu = :gu " +
                "ORDER BY d "
    )
    fun getStoreListByRegion(curLat: Double, curLng: Double, gu: String) : PagingSource<Int, StoreItem>

    //조건에 따른 착한가격업소 목록 불러오기
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
    ) : List<StoreMapItemDTO>

}