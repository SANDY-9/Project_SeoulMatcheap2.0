package com.sandy.matcheap.domain.bookmark.repository

interface BookmarkRepository {

    suspend fun addBookmark(id: String, code: String)
    suspend fun deleteBookmark(id: String)

}