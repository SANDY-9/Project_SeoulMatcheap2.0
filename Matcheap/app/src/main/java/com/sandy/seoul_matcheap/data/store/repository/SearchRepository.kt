package com.sandy.seoul_matcheap.data.store.repository

import androidx.room.Transaction
import com.sandy.seoul_matcheap.data.store.dao.SearchDao
import com.sandy.seoul_matcheap.data.store.entity.SearchHistory
import com.sandy.seoul_matcheap.util.helper.DataHelper
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-05-03
 * @desc
 */
class SearchRepository @Inject constructor(private val dao: SearchDao) {

    // !-- get search result
    @Transaction
    suspend fun requestSearchStore(param: String) = dao.run {
        insertSearchHistory(SearchHistory(param.trim(), "${LocalDateTime.now()}"))
        requestSearchStore(DataHelper.removeSpace(param))
    }


    // !-- autoComplete
    fun downloadAutoCompleteList() = dao.getAutoCompleteList()


    // !-- search history
    suspend fun deleteSearchHistory(param: String) = dao.deleteSearchHistory(param.trim())
    suspend fun clearSearchHistory() = dao.clearSearchHistory()
    fun downloadSearchHistoryList() = dao.getSearchHistoryList()

}