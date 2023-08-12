package com.sandy.seoul_matcheap.ui.more.notice

import androidx.lifecycle.*
import androidx.paging.*
import com.sandy.seoul_matcheap.data.matcheap.NoticePagingSource
import com.sandy.seoul_matcheap.util.constants.PAGE_SIZE

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-30
 * @desc
 */

class NoticeViewModel : ViewModel() {

    val noticeList = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        NoticePagingSource()
    }.liveData.cachedIn(viewModelScope)

}