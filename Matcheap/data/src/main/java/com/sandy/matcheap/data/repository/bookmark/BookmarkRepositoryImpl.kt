package com.sandy.matcheap.data.repository.bookmark

import androidx.room.Transaction
import com.sandy.matcheap.common.DEFAULT_
import com.sandy.matcheap.data.mappers.toDomain
import com.sandy.matcheap.data.room.dao.BookmarkDao
import com.sandy.matcheap.data.room.dao.MenuDao
import com.sandy.matcheap.data.room.entity.Bookmark
import com.sandy.matcheap.domain.bookmark.repository.BookmarkRepository
import com.sandy.matcheap.domain.menu.repository.GetStoreMenuRepository
import com.sandy.matcheap.domain.model.menu.Menu
import com.sandy.matcheap.domain.model.store.BookmarkStoreDetails
import com.sandy.matcheap.domain.model.store.StoreDetails
import kotlinx.coroutines.flow.Flow
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

    override fun getBookmarkList(): Flow<List<BookmarkStoreDetails>> {
        return bookmarkDao.getBookmarkList().toDomain()
    }

    override fun getBookmarkedStoreTotalCount(): Flow<Int> {
        return bookmarkDao.getBookmarkedStoreCount()
    }

    override fun getBookmarkedStoreCount(code: String): Flow<Int> {
        return bookmarkDao.getBookmarkedStoreCountByCode(code)
    }
}