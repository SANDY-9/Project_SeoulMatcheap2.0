package com.sandy.matcheap.data.repository.bookmark

import androidx.room.Transaction
import com.sandy.matcheap.data.room.dao.BookmarkDao
import com.sandy.matcheap.data.room.dao.MenuDao
import com.sandy.matcheap.data.room.entity.Bookmark
import com.sandy.matcheap.domain.repository.bookmark.BookmarkRepository
import com.sandy.matcheap.domain.repository.menu.GetStoreMenuRepository
import java.time.LocalDateTime

class BookmarkRepositoryImpl(
    private val bookmarkDao: BookmarkDao,
    private val menuDao: MenuDao,
    private val getMenuRepository: GetStoreMenuRepository
): BookmarkRepository {

    @Transaction
    override suspend fun addBookmark(id: String, code: String) {
        val menus = getMenuRepository.getMenu(id)
        val bookmark = Bookmark(
            id = id,
            code = code,
            bookmarked = true,
            bookmarkDate = "${LocalDateTime.now()}"
        )
        menuDao.addMenu(menus)
        bookmarkDao.addBookmark(bookmark)
    }

    @Transaction
    override suspend fun deleteBookmark(id: String) {
        menuDao.deleteMenu(id)
        bookmarkDao.deleteBookmark(id)
    }

}