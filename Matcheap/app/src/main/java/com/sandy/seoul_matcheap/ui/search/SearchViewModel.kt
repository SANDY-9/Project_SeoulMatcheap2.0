package com.sandy.seoul_matcheap.ui.search

import androidx.lifecycle.*
import androidx.paging.*
import com.sandy.seoul_matcheap.data.store.dao.AutoComplete
import com.sandy.seoul_matcheap.data.store.dao.StoreListItem
import com.sandy.seoul_matcheap.data.store.entity.SearchHistory
import com.sandy.seoul_matcheap.data.store.repository.SearchRepository
import com.sandy.seoul_matcheap.util.helper.DataHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2022-02-04
 * @desc
 */

private typealias RecommendWords = List<String>

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) : ViewModel() {

    //search history
    val historyList: LiveData<List<SearchHistory>> by lazy { searchRepository.downloadSearchHistoryList() }

    fun removeHistory(param: String) = viewModelScope.launch(Dispatchers.IO) {
        searchRepository.deleteSearchHistory(param)
    }
    fun clearHistory() = viewModelScope.launch(Dispatchers.IO) {
        searchRepository.clearSearchHistory()
    }

    //autocomplete
    private val autoCompleteList by lazy { searchRepository.downloadAutoCompleteList() }

    val input = MutableLiveData<String>()
    val filteredAutoCompleteList = autoCompleteList.switchMap { list ->
        input.map {
            getFilteredAutoCompleteList(it, list)
        }
    }
    private fun getFilteredAutoCompleteList(input: String, list:List<AutoComplete>) = list.filter {
        if(input.isBlank()) return@filter false

        val target = DataHelper.removeSpace(it.name)
        val convertedInput = DataHelper.removeSpace(input)
        target.contains(convertedInput)
    }

    //!-- recommend word
    private val recommendWordList = listOf(
        "미용실",	"설렁탕",	"헤어",	"냉면",	"국수",	"세탁",	"스튜디오",	"해물",
        "삼겹살",	"해장국",	"세탁소",	"헤어샵",	"구이",	"이발",	"갈비",
        "김밥",	"고기",	"식당",	"다방",	"칼국수",	"닭한마리",	"중화요리",	"목욕탕",
        "탕수육",	"돈까스",	"짜장",	"분식",	"보쌈",	"생고기",	"숯불갈비",	"한우",
        "이용원",	"커피",	"뚝배기",	"모텔",	"부대찌개",	"국밥",	"찌개",	"콩나물국밥",
        "치킨",	"짬뽕",	"돼지",	"오리",	"초밥",	"청국장",	"우동",	"쌈밥",	"만두",
        "화로구이",	"곱창",	"갈비찜",	"불냉면",	"장어",	"불고기",	"메밀",	"족발",
        "비빔밥",	"기사식당",	"소머리국밥",	"순대국",	"감자탕",	"부페",	"함흥냉면",
        "솥뚜껑삼겹살",	"닭곰탕",	"추어탕",	"보리밥",	"순대",	"정육점",	"밥집",
        "등갈비",	"곰탕",	"갈비탕",	"도시락",	"중국요리",	"호프",	"백숙",
        "카페",	"매운탕",	"바비큐",	"한식",	"시래기",	"파스타",	"아구찜",	"꽈배기",
        "떡볶이",	"두부",	"멸치국수",	"카레",	"오겹살"
    )
    private val _recommendWords = MutableLiveData(recommendWordList.shuffled())
    val recommendWords : LiveData<RecommendWords> = _recommendWords
    fun updateRecommendList() {
        _recommendWords.value = recommendWordList.shuffled()
    }

    //!-- search
    private val _searchResultList = MutableLiveData<PagingSource<Int, StoreListItem>>()
    val searchResultList = _searchResultList.switchMap {
        Pager(PagingConfig(pageSize = 10, prefetchDistance = 1, maxSize = 50)) {
            it
        }.liveData.cachedIn(viewModelScope)
    }
    fun requestSearch(param: String) = viewModelScope.launch(Dispatchers.IO) {
        _searchResultList.postValue(
            searchRepository.requestSearchStore(param)
        )
    }

}