package com.sandy.seoul_matcheap.ui.more.bookmark

import androidx.lifecycle.*
import com.sandy.matcheap.common.MESSAGE_ERROR
import com.sandy.matcheap.common.MESSAGE_NETWORK_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.bookmark.use_case.BookmarkUseCases
import com.sandy.seoul_matcheap.extensions.onIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-05
 * @desc
 */

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkUseCases: BookmarkUseCases
) : ViewModel() {

    private val _bookmarkListState = MutableLiveData<BookmarkListState>()
    val bookmarkListState: LiveData<BookmarkListState> = _bookmarkListState

    private val _bookmarkCountState = MutableLiveData<BookmarkCountState>()
    val bookmarkCountState: LiveData<BookmarkCountState> = _bookmarkCountState

    private val _addBookmarkState = MutableLiveData<AddBookmarkState>()
    val addBookmarkState: LiveData<AddBookmarkState> = _addBookmarkState

    private val _deleteBookmarkState = MutableLiveData<DeleteBookmarkState>()
    val deleteBookmarkState: LiveData<DeleteBookmarkState> = _deleteBookmarkState

    fun updateBookmarkState(code: String) {
        onIO {
            bookmarkUseCases.getBookmarkList().onEach { result ->
                _bookmarkListState.postValue(
                    BookmarkListState(data = result)
                )
            }.launchIn(this)
        }
        onIO {
            bookmarkUseCases.getBookmarkList.getBookmarkedStoreCount(code).onEach { result ->
                _bookmarkCountState.postValue(
                    BookmarkCountState(data = result)
                )
            }.launchIn(this)
        }
    }

    fun addBookmark(id: String, code: String) {
        onIO {
            bookmarkUseCases.addBookmark(id, code).onEach { result ->
                _addBookmarkState.postValue(
                    when(result) {
                        is Resource.Success -> AddBookmarkState(data = result.data)
                        is Resource.Error -> AddBookmarkState(error = result.message ?: MESSAGE_NETWORK_ERROR)
                        is Resource.Loading -> AddBookmarkState(isLoading = true)
                    }
                )
            }.launchIn(this)
        }
    }

    fun deleteBookmark(id: String) {
        onIO {
            bookmarkUseCases.deleteBookmark(id).onEach { result ->
                when(result) {
                    is Resource.Success -> DeleteBookmarkState(data = result.data)
                    is Resource.Error -> DeleteBookmarkState(error = result.message ?: MESSAGE_ERROR)
                    is Resource.Loading -> DeleteBookmarkState(isLoading = true)
                }
            }.launchIn(this)
        }
    }

}