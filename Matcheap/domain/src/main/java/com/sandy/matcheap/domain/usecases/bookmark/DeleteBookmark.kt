package com.sandy.matcheap.domain.usecases.bookmark

import com.sandy.matcheap.common.MESSAGE_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.repository.bookmark.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteBookmark @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(id: String): Flow<Resource<String?>> = flow {
        emit(Resource.Loading())
        try {
            bookmarkRepository.deleteBookmark(id)
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(MESSAGE_ERROR))
        }
    }

}