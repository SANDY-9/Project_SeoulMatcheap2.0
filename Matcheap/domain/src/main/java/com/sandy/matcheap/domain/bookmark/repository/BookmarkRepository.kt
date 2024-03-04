package com.sandy.matcheap.domain.bookmark.repository

import com.sandy.matcheap.domain.model.store.BookmarkStoreDetails
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {

    suspend fun addBookmark(id: String, code: String)
    suspend fun deleteBookmark(id: String)
    fun getBookmarkList(): Flow<List<BookmarkStoreDetails>>
    fun getBookmarkedStoreTotalCount(): Flow<Int>
    fun getBookmarkedStoreCount(code: String): Flow<Int>

}