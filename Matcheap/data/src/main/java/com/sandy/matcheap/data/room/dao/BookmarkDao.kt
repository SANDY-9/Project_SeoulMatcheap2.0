package com.sandy.matcheap.data.room.dao

import androidx.room.*
import com.sandy.matcheap.data.remote.menu.model.MenuDTO
import com.sandy.matcheap.data.room.entity.Bookmark

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

}
