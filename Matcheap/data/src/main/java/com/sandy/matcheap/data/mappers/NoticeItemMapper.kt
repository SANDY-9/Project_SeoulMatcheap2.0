package com.sandy.matcheap.data.mappers

import com.sandy.matcheap.common.NOTICE_BASE_URL
import com.sandy.matcheap.data.utils.DataHelper
import com.sandy.matcheap.domain.model.notice.Notice
import org.jsoup.select.Elements

fun Elements.toDomain(): List<Notice> {
    return map {
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
}

private const val TITLE_PARSING_TAG = "strong[class=title ell]"
private const val DATE_PARSING_TAG = "span[class=date]"
private const val URL_PARSING_TAG = "a[class=link pcol2]"
private const val URL_PARSING_ATTR = "href"