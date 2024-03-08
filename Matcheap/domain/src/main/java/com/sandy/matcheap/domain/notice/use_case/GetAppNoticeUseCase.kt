package com.sandy.matcheap.domain.notice.use_case

import androidx.paging.PagingData
import com.sandy.matcheap.domain.model.notice.Notice
import com.sandy.matcheap.domain.notice.repository.GetAppNoticeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class GetAppNoticeUseCase @Inject constructor(
    private val appNoticeRepository: GetAppNoticeRepository
) {

    suspend operator fun invoke(pageSize: Int, scope: CoroutineScope): StateFlow<PagingData<Notice>> {
        return appNoticeRepository.getAppNoticePagingList(pageSize, scope).stateIn(scope)
    }

}