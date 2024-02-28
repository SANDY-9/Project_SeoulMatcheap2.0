package com.sandy.matcheap.domain.usecases.bookmark

import com.sandy.matcheap.common.MESSAGE_NETWORK_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.repository.bookmark.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddBookmark @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(id: String, code: String): Flow<Resource<String?>> = flow {
        emit(Resource.Loading())
        try {
            bookmarkRepository.addBookmark(id, code)
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(MESSAGE_NETWORK_ERROR))
        }
    }

}