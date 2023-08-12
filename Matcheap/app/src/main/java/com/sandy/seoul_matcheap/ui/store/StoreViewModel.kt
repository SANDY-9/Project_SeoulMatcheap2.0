package com.sandy.seoul_matcheap.ui.store

import androidx.lifecycle.*
import androidx.paging.*
import com.sandy.seoul_matcheap.data.store.SeoulOpenAPIDataSource
import com.sandy.seoul_matcheap.data.store.dao.StoreDetails
import com.sandy.seoul_matcheap.data.store.entity.Menu
import com.sandy.seoul_matcheap.data.store.repository.StoreRepository
import com.sandy.seoul_matcheap.util.constants.ConnectState
import com.sandy.seoul_matcheap.util.constants.PAGE_DISTANCE
import com.sandy.seoul_matcheap.util.constants.PAGE_SIZE
import com.sandy.seoul_matcheap.util.constants.TYPE_REGION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-21
 * @desc
 */

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val seoulOpenAPIDataSource: SeoulOpenAPIDataSource
    ) : ViewModel() {

    private val _loadingState = MutableLiveData(ConnectState.NONE)
    val loadingState : LiveData<ConnectState> = _loadingState
    private fun setLoadingState(state: ConnectState) {
        _loadingState.postValue(state)
    }

    // !-- store details
    private val _store = MutableLiveData<StoreDetails>()
    val store: LiveData<StoreDetails> = _store
    private val _menu = MutableLiveData<List<Menu>>()
    val menu: LiveData<List<Menu>> = _menu

    fun updateStoreDetails(id: String) = viewModelScope.launch(Dispatchers.IO) {
        setLoadingState(ConnectState.ING)
        _store.postValue(
            storeRepository.downloadStoreDetails(id)
        )
        updateMenu(id)
    }

    private suspend fun updateMenu(id: String) {
        when (val result = seoulOpenAPIDataSource.fetchMenu(id)) {
            null -> setLoadingState(ConnectState.FAIL)
            else -> {
                _menu.postValue(result!!)
                setLoadingState(ConnectState.SUCCESS)
            }
        }
    }

    fun getCallNumber() = store.value?.run { "tel:" + store.tel }


    // !-- store list
    private val gu = MutableLiveData<String?>()
    val storeList = gu.switchMap {
        Pager(
            PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                maxSize = PAGE_SIZE * PAGE_DISTANCE
            )
        ) {
            storeRepository.downloadStoreList(it)
        }.liveData.cachedIn(viewModelScope)
    }

    fun downloadStoreList(type: Int, gu: String) {
        this.gu.value = if(type == TYPE_REGION) gu else null
    }

    private val _storeCount = MutableLiveData<Int>()
    val storeCount: LiveData<Int> = _storeCount

    fun updateStoreCount(type: Int, gu: String, position: Int) = viewModelScope.launch(Dispatchers.IO) {
        val count = storeRepository.downloadStoreCount(
            code = position.toString(),
            gu = if(type == TYPE_REGION) gu else null
        )
        _storeCount.postValue(count)
    }

}