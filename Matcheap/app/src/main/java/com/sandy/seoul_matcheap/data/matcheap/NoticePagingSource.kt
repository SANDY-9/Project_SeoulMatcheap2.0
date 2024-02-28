package com.sandy.seoul_matcheap.data.matcheap

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sandy.seoul_matcheap.util.constants.MATCHEAP_NOTICE_URL
import com.sandy.seoul_matcheap.util.constants.NOTICE_BASE_URL
import com.sandy.matcheap.data.utils.DataHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-30
 * @desc
 */
class NoticePagingSource : PagingSource<Int, Notice>() {

    override fun getRefreshKey(state: PagingState<Int, Notice>) = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notice> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            val data = fetchNoticeList(page)!!
            LoadResult.Page(
                data = data.first,
                prevKey = if(page == FIRST_PAGE_INDEX) null else page - 1,
                nextKey = if(page >= data.second) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun fetchNoticeList(page: Int) = withContext(Dispatchers.IO) {
        try {
            Jsoup.connect(MATCHEAP_NOTICE_URL + page).get().run {
                val elements = select(ELEMENTS_PARSING_TAG)
                val lastPage = select(PAGE_PARSING_TAG).last()?.text()?.toInt() ?: FIRST_PAGE_INDEX

                getNoticeList(elements) to lastPage
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getNoticeList(elements: Elements) = elements.map {
        val str = it.select(TITLE_PARSING_TAG).text().split("] ")
        val date = it.select(DATE_PARSING_TAG).text()
        val url = it.select(URL_PARSING_TAG).attr(URL_PARSING_ATTR)
        Notice(
            category = str[0].substring(1),
            title = str[1],
            date = DataHelper.convertDateFormat(date),
            url = NOTICE_BASE_URL + url
        )
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
        private const val ELEMENTS_PARSING_TAG = "ul[class=thumblist] li"
        private const val PAGE_PARSING_TAG = "div[class=blog2_paginate] a"
        private const val TITLE_PARSING_TAG = "strong[class=title ell]"
        private const val DATE_PARSING_TAG = "span[class=date]"
        private const val URL_PARSING_TAG = "a[class=link pcol2]"
        private const val URL_PARSING_ATTR = "href"
    }
}

data class Notice(
    val category: String?,
    val title: String?,
    val date: String?,
    val url: String?
)