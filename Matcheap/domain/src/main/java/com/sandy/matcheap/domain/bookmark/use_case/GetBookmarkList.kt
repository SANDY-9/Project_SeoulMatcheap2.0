package com.sandy.matcheap.domain.bookmark.use_case

import com.sandy.matcheap.common.DEFAULT_
import com.sandy.matcheap.domain.bookmark.repository.BookmarkRepository
import com.sandy.matcheap.domain.model.store.BookmarkStoreDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkList @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {

    operator fun invoke(): Flow<List<BookmarkStoreDetails>> {
        return bookmarkRepository.getBookmarkList()
    }

    fun getBookmarkedStoreCount(code: String = DEFAULT_): Flow<Int> {
        return when(code) {
            // code의 값이 DEFAULT_ ="0"이면 북마크에 저장된 전체 데이터를 가져와야 한다.
            DEFAULT_ -> bookmarkRepository.getBookmarkedStoreTotalCount()
            else -> bookmarkRepository.getBookmarkedStoreCount(code)
        }
    }

}