package com.sandy.seoul_matcheap.ui.more.notice

import androidx.lifecycle.*
import androidx.paging.*
import com.sandy.matcheap.data.paging.NoticePagingSource
import com.sandy.matcheap.domain.model.notice.Notice
import com.sandy.matcheap.domain.notice.use_case.GetAppNoticeUseCase
import com.sandy.seoul_matcheap.util.constants.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-30
 * @desc
 */

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val getAppNoticeUseCase: GetAppNoticeUseCase
): ViewModel() {

    suspend fun getNoticeList(): StateFlow<PagingData<Notice>> = getAppNoticeUseCase(PAGE_SIZE, viewModelScope)

}