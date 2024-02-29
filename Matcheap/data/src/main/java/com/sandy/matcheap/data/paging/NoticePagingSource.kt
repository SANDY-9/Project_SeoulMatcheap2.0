package com.sandy.matcheap.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sandy.matcheap.data.remote.notice.AppNoticeDataSource.Companion.FIRST_PAGE_INDEX
import com.sandy.matcheap.data.repository.notice.GetAppNoticeRepositoryImpl
import com.sandy.matcheap.domain.model.notice.Notice
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-30
 * @desc
 */
class NoticePagingSource @Inject constructor(
    private val appNoticeRepository: GetAppNoticeRepositoryImpl
) : PagingSource<Int, Notice>() {

    override fun getRefreshKey(state: PagingState<Int, Notice>) = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notice> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            val data = appNoticeRepository.getAppNoticeList(page)!!.data!!
            LoadResult.Page(
                data = data.first,
                prevKey = if(page == FIRST_PAGE_INDEX) null else page - 1,
                nextKey = if(page >= data.second) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
