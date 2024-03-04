package com.sandy.matcheap.data.room.dao

import androidx.room.*
import com.sandy.matcheap.data.remote.menu.model.MenuDTO
import com.sandy.matcheap.data.room.dto.BookmarkedStoreDTO
import com.sandy.matcheap.data.room.entity.Bookmark
import kotlinx.coroutines.flow.Flow

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

    // !-- delete bookmark
    @Query("DELETE FROM store_bookmark WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    //북마크에 저장된 착한가격업소 목록 불러오기
    @Transaction
    @Query("SELECT * FROM store_bookmark ")
    fun getBookmarkList() : Flow<List<BookmarkedStoreDTO>>

    // 북마크로 저장한 착한가격업소 총 개수 반환
    @Query("SELECT count(*) FROM store_bookmark")
    fun getBookmarkedStoreCount() : Flow<Int>


    // 북마크로 저장한 착한가격업소 카테고리 조건에 따라 개수 반환
    @Query(
        "SELECT count(*) " +
                "FROM store_bookmark " +
                "WHERE code = :code "
    )
    fun getBookmarkedStoreCountByCode(code: String) : Flow<Int>

}
