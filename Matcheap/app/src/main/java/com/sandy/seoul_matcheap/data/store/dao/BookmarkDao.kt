package com.sandy.seoul_matcheap.data.store.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sandy.seoul_matcheap.data.store.entity.Bookmark
import com.sandy.seoul_matcheap.data.store.entity.Menu
import com.sandy.seoul_matcheap.data.store.entity.StoreInfo

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-02
 * @desc
 */
@Dao
interface BookmarkDao {


    // !-- add bookmark
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: Bookmark)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMenu(menus: List<Menu>)


    // !-- delete bookmark
    @Query("DELETE FROM store_bookmark WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    @Query("DELETE FROM store_menu WHERE id = :id")
    suspend fun deleteMenu(id: String)


    // !-- select bookmark
    @Transaction
    @Query("SELECT * FROM store_bookmark ")
    fun getBookmarkList() : LiveData<List<BookmarkedStore>>

    @Query("SELECT count(*) FROM store_bookmark")
    fun getBookmarkCount() : LiveData<Int>

    @Query(
        "SELECT count(*) " +
                "FROM store_bookmark " +
                "WHERE code = :code "
    )

    fun getBookmarkCountByCode(code: String) : LiveData<Int>

}

data class BookmarkedStore(
    @Embedded
    val bookmark: Bookmark,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val store: StoreInfo,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val menu: List<Menu>?
)