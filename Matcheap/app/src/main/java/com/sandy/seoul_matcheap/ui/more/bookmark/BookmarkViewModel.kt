package com.sandy.seoul_matcheap.ui.more.bookmark

import androidx.lifecycle.*
import com.sandy.seoul_matcheap.data.store.dao.BookmarkedStore
import com.sandy.seoul_matcheap.data.store.repository.BookmarkRepository
import com.sandy.seoul_matcheap.util.constants.ConnectState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-05
 * @desc
 */

@HiltViewModel
class BookmarkViewModel @Inject constructor(private val bookmarkRepository: BookmarkRepository) : ViewModel() {

    private val _loadingState = MutableLiveData(ConnectState.NONE)
    val loadingState : LiveData<ConnectState> = _loadingState
    private fun setLoadingState(state: ConnectState) {
        _loadingState.postValue(state)
    }


    val bookmarkList: LiveData<List<BookmarkedStore>> by lazy { bookmarkRepository.downloadBookmarkList() }

    private val code = MutableLiveData<String>()
    val bookmarkCount = code.switchMap {
        bookmarkRepository.downloadBookmarkCount(it)
    }
    fun updateBookmarkCount(code: String) {
        this.code.value = code
    }

    fun updateBookmark(id: String, code: String, bookmarked: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        setLoadingState(ConnectState.ING)
        when {
            bookmarked -> addBookmark(id, code)
            else -> deleteBookmark(id)
        }
    }
    private suspend fun addBookmark(id: String, code: String) {
        val result = bookmarkRepository.addBookmark(id, code)
        setLoadingState(if(result) ConnectState.SUCCESS else ConnectState.FAIL)
    }
    private suspend fun deleteBookmark(id: String) {
        bookmarkRepository.deleteBookmark(id)
    }

}