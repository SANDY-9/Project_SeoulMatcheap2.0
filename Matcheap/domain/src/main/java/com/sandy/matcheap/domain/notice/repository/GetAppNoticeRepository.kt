package com.sandy.matcheap.domain.notice.repository

import androidx.paging.PagingData
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.model.notice.Notice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface GetAppNoticeRepository {

    fun getAppNoticePagingList(pageSize: Int, scope: CoroutineScope): Flow<PagingData<Notice>>


}