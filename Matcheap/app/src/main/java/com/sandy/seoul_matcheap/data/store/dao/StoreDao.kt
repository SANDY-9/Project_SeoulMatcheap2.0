package com.sandy.seoul_matcheap.data.store.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.sandy.seoul_matcheap.data.store.entity.Bookmark
import com.sandy.seoul_matcheap.data.store.entity.StoreInfo
import java.io.Serializable

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
    suspend fun insertStores(stores: List<StoreInfo>)


    //!-- select store details
    @Transaction
    @Query("SELECT * FROM store_info WHERE id = :id")
    suspend fun getStoreDetails(id: String) : StoreDetails


    //!-- select store list
    @Query(
        "SELECT id, code, name, address, photo, lat, lng, $CURRENT_LOCATION_QUERY, $DISTANCE_QUERY " +
                "FROM store_info " +
                "ORDER BY d " +
                "LIMIT 5"
    )
    suspend fun getSurroundingStores(curLat: Double, curLng: Double): List<StoreItem>

    @Query(
        "SELECT id, code, codeName, name, address, content, photo, lat, lng " +
                "FROM store_info " +
                "WHERE photo != 'http://tearstop.seoul.go.kr/mulga/photo/' " +
                "ORDER BY RANDOM() " +
                "LIMIT 2"
    )
    suspend fun getRandomStores() : List<RandomStore>

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

    @Query(
        "SELECT id, code, name, address, photo, lat, lng, $CURRENT_LOCATION_QUERY, $DISTANCE_QUERY " +
                "FROM store_info " +
                "WHERE gu = :gu " +
                "ORDER BY d "
    )
    fun getStoreListByRegion(curLat: Double, curLng: Double, gu: String) : PagingSource<Int, StoreItem>


    //!-- select recommend store
    @Query(
        "SELECT id, name " +
                "FROM store_info " +
                "WHERE code IN (1, 2, 3, 4) " +
                "ORDER BY RANDOM() "
    )
    suspend fun getRecommendStore() : RecommendStore


    // !-- select storeCount
    @Query(
        "SELECT count(*), lat, lng, $DISTANCE_QUERY " +
                "FROM store_info " +
                "WHERE d <= :r "
    )
    suspend fun getStoreCountByDistance(curLat: Double, curLng: Double, r: Double) : Count

    @Query(
        "SELECT count(*), lat, lng, $DISTANCE_QUERY " +
                "FROM store_info " +
                "WHERE d <= :r " +
                "AND code = :code"
    )
    suspend fun getStoreCountByDistanceAndCode(curLat: Double, curLng: Double, r: Double, code: String) : Count

    @Query(
        "SELECT count(*) " +
                "FROM store_info " +
                "WHERE gu = :gu "
    )
    suspend fun getStoreCountByGu(gu: String) : Int

    @Query(
        "SELECT count(*) " +
                "FROM store_info " +
                "WHERE gu = :gu " +
                "AND code = :code "
    )
    suspend fun getStoreCountByGuAndCode(gu: String, code: String) : Int

    @Query(
        "SELECT codeName, count(*) " +
                "FROM store_info " +
                "GROUP BY codeName " +
                "ORDER BY code "
    )
    suspend fun getStoreTotalCountForCode() : List<StoreTotalCount>

    @Query("SELECT count(*) FROM store_info ")
    suspend fun getStoreTotalCount() : Int

}

// !-- data class
data class StoreDetails(
    @Embedded
    val store: StoreInfo,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val bookmark: Bookmark?
)

data class RecommendStore(
    val id: String,
    val name: String
)

data class StoreItem(
    val id: String,
    val code: String,
    val	name: String,
    val	address: String,
    val	photo: String,
    val lat: Double,
    val lng: Double,
    val curLat: Double,
    val curLng: Double,
    val d: Float
) : Serializable

data class RandomStore(
    val id: String,
    val code: String,
    val codeName: String,
    val	name: String,
    val	address: String,
    val content: String,
    val	photo: String,
    val lat: Double,
    val lng: Double
)

data class Count(
    @ColumnInfo(name = "count(*)") val count: Int,
    val lat: Double,
    val lng: Double,
    val d: Float
)

data class StoreTotalCount(
    @ColumnInfo(name = "codeName") val category: String,
    @ColumnInfo(name = "count(*)") val count: Int
)