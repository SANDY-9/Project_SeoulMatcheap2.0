package com.sandy.matcheap.domain.usecases.notice

import androidx.paging.PagingData
import com.sandy.matcheap.domain.model.notice.Notice
import com.sandy.matcheap.domain.repository.notice.GetAppNoticeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppNoticeUseCase @Inject constructor(
    private val appNoticeRepository: GetAppNoticeRepository
) {

    operator fun invoke(pageSize: Int, scope: CoroutineScope): Flow<PagingData<Notice>> {
        return appNoticeRepository.getAppNoticePagingList(pageSize, scope)
    }

}