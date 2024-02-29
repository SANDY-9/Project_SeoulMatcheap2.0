package com.sandy.matcheap.data.remote.notice

import com.sandy.matcheap.common.MATCHEAP_NOTICE_URL
import com.sandy.matcheap.common.MESSAGE_NETWORK_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.data.mappers.toDomain
import com.sandy.matcheap.domain.model.notice.Notice
import org.jsoup.Jsoup

class AppNoticeDataSource {

    fun fetchAppNoticeList(page: Int) : Resource<Pair<List<Notice>, Int>> {
        return try {
            Jsoup.connect(MATCHEAP_NOTICE_URL + page).get().run {
                val elements = select(ELEMENTS_PARSING_TAG)
                val lastPage = select(PAGE_PARSING_TAG).last()?.text()?.toInt() ?: FIRST_PAGE_INDEX
                Resource.Success(elements.toDomain() to lastPage)
            }
        } catch (e: Exception) {
            Resource.Error(MESSAGE_NETWORK_ERROR)
        }
    }

    companion object {
        const val FIRST_PAGE_INDEX = 1
        private const val ELEMENTS_PARSING_TAG = "ul[class=thumblist] li"
        private const val PAGE_PARSING_TAG = "div[class=blog2_paginate] a"
    }
}