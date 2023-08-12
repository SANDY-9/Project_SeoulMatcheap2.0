package com.sandy.seoul_matcheap.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandy.seoul_matcheap.data.store.dao.StoreTotalCount
import com.sandy.seoul_matcheap.data.store.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-18
 * @desc
 */

@HiltViewModel
class SeeMoreViewModel @Inject constructor(storeRepository: StoreRepository): ViewModel() {
    private val _storeCounts = MutableLiveData<List<StoreTotalCount>>()
    val storeCounts: LiveData<List<StoreTotalCount>> = _storeCounts

    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> = _totalCount

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val storeCounts = storeRepository.downloadStoreTotalCountForCode()
            val totalCount = storeRepository.downloadStoreTotalCount()
            _storeCounts.postValue(storeCounts)
            _totalCount.postValue(totalCount)
        }
    }
}