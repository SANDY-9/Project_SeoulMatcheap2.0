package com.sandy.seoul_matcheap.data.store.repository

import androidx.lifecycle.map
import androidx.room.Transaction
import com.sandy.seoul_matcheap.data.store.SeoulOpenAPIDataSource
import com.sandy.seoul_matcheap.data.store.dao.BookmarkDao
import com.sandy.seoul_matcheap.data.store.entity.Bookmark
import com.sandy.seoul_matcheap.util.constants.DEFAULT_
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-03
 * @desc
 */
class BookmarkRepository @Inject constructor(
    private val dao: BookmarkDao,
    private val dataSource: SeoulOpenAPIDataSource) {


    // !-- add bookmark
    @Transaction
    suspend fun addBookmark(id: String, code: String) = dao.run {
        val menus = dataSource.fetchMenu(id)
        menus?.let {
            val bookmark = Bookmark(
                id = id,
                code = code,
                bookmarked = true,
                bookmarkDate = "${LocalDateTime.now()}"
            )
            addMenu(it)
            addBookmark(bookmark)
        }
        menus != null
    }


    // !-- delete bookmark
    @Transaction
    suspend fun deleteBookmark(id: String) = dao.run {
        deleteMenu(id)
        deleteBookmark(id)
    }


    // !-- select bookmark
    fun downloadBookmarkList() = dao.getBookmarkList().map {
        it.sortedByDescending { store ->
            store.bookmark.bookmarkDate
        }
    }

    fun downloadBookmarkCount(code: String = DEFAULT_) = dao.run {
        if(code == DEFAULT_) getBookmarkCount() else getBookmarkCountByCode(code)
    }

}