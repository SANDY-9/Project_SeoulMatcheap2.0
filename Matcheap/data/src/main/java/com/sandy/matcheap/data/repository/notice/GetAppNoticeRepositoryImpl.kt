package com.sandy.matcheap.data.repository.notice

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.data.paging.NoticePagingSource
import com.sandy.matcheap.data.remote.notice.AppNoticeDataSource
import com.sandy.matcheap.domain.model.notice.Notice
import com.sandy.matcheap.domain.repository.notice.GetAppNoticeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppNoticeRepositoryImpl @Inject constructor(
    private val appNoticeDataSource: AppNoticeDataSource
): GetAppNoticeRepository {
    override fun getAppNoticePagingList(pageSize: Int, scope: CoroutineScope): Flow<PagingData<Notice>> {
        return Pager(PagingConfig(pageSize = pageSize)) {
            NoticePagingSource(this)
        }.flow.cachedIn(scope)
    }

   fun getAppNoticeList(page: Int): Resource<Pair<List<Notice>, Int>> {
        return appNoticeDataSource.fetchAppNoticeList(page)
   }

}