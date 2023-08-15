package com.sandy.seoul_matcheap.data.store.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.sandy.seoul_matcheap.data.store.entity.SearchHistory

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-02
 * @desc
 */
@Dao
interface SearchDao {

    // !-- request search
    @Query(
        "SELECT id, code, name, address, photo, lat, lng, $CURRENT_LOCATION_QUERY, $DISTANCE_QUERY " +
                "FROM store_info " +
                "WHERE REPLACE(name, ' ', '') " +
                "LIKE '%' || :param || '%' " +
                "ORDER BY d"
    )
    fun requestSearchStore(param: String, curLat:Double, curLng:Double) : PagingSource<Int, StoreItem>


    // !-- autoComplete
    @Query("SELECT name, address FROM store_info")
    fun getAutoCompleteList(): LiveData<List<AutoComplete>>


    // !-- search history
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(history: SearchHistory)

    @Query("DELETE FROM search_history WHERE name = :name")
    suspend fun deleteSearchHistory(name: String)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    @Query("SELECT * FROM search_history ORDER BY date DESC")
    fun getSearchHistoryList(): LiveData<List<SearchHistory>>

}

data class AutoComplete(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "address") val address: String
)