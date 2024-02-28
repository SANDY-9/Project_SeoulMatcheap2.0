package com.sandy.matcheap.domain.repository.bookmark

interface BookmarkRepository {

    suspend fun addBookmark(id: String, code: String)
    suspend fun deleteBookmark(id: String)

}