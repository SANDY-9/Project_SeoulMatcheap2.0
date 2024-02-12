package com.sandy.seoul_matcheap.ui.home

import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.ItemRvSpinnerBinding
import com.sandy.seoul_matcheap.ui.common.BaseListAdapter
import com.sandy.seoul_matcheap.util.constants.TYPE_CATEGORY
import com.sandy.seoul_matcheap.util.constants.TYPE_REGION

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2022-05-12
 * @desc
 */
class RegionSpinnerAdapter(type: Int = TYPE_CATEGORY) : BaseListAdapter<ItemRvSpinnerBinding, String>(R.layout.item_rv_spinner) {

    init {
        val list = listOf(
            "전체보기",
            "강남구",
            "강동구",
            "강북구",
            "강서구",
            "관악구",
            "광진구",
            "구로구",
            "금천구",
            "노원구",
            "도봉구",
            "동대문구",
            "동작구",
            "마포구",
            "서대문구",
            "서초구",
            "성동구",
            "성북구",
            "송파구",
            "양천구",
            "영등포구",
            "용산구",
            "은평구",
            "종로구",
            "중구",
            "중랑구"
        )
        submitList(
            if(type == TYPE_REGION) list
            else list.subList(TYPE_CATEGORY, list.lastIndex)
        )
    }

    override fun ItemRvSpinnerBinding.setBinding(item: String, position: Int) {
        tvName.apply {
            text = item
            setOnClickListener {
                onItemClick?.let { it(item) }
            }
        }
    }

}