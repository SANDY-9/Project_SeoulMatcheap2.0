package com.sandy.seoul_matcheap.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sandy.matcheap.common.MESSAGE_ERROR
import com.sandy.matcheap.common.MESSAGE_NETWORK_ERROR
import com.sandy.matcheap.common.Resource
import com.sandy.matcheap.domain.menu.use_case.GetStoreMenuUseCase
import com.sandy.matcheap.domain.store.use_case.GetStoreDetailsUseCase
import com.sandy.seoul_matcheap.extensions.onIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StoreDetailsViewModel @Inject constructor(
    private val getStoreDetailsUseCase: GetStoreDetailsUseCase,
    private val getStoreMenuUseCase: GetStoreMenuUseCase
): ViewModel() {

    private val _storeDetailsState = MutableLiveData<StoreDetailsState>()
    val storeDetailsState: LiveData<StoreDetailsState> = _storeDetailsState

    private val _storeMenuListState = MutableLiveData<StoreMenuListState>()
    val storeMenuListState: LiveData<StoreMenuListState> = _storeMenuListState

    fun updateStoreDetails(id: String) {
        onIO {
            getStoreDetailsUseCase(id).onEach { result ->
                _storeDetailsState.postValue(
                    when(result) {
                        is Resource.Success -> StoreDetailsState(data = result.data)
                        is Resource.Error -> StoreDetailsState(error = result.message ?: MESSAGE_ERROR)
                        is Resource.Loading -> StoreDetailsState(isLoading = true)
                    }
                )
            }.launchIn(this)
        }

        onIO {
            getStoreMenuUseCase(id).onEach { result ->
                _storeMenuListState.postValue(
                    when(result) {
                        is Resource.Success -> StoreMenuListState(data = result.data ?: emptyList())
                        is Resource.Error -> StoreMenuListState(error = result.message ?: MESSAGE_NETWORK_ERROR)
                        is Resource.Loading -> StoreMenuListState(isLoading = true)
                    }
                )
            }.launchIn(this)
        }
    }



}