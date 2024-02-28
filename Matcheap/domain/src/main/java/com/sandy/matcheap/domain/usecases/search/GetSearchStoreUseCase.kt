package com.sandy.matcheap.domain.usecases.search

import androidx.paging.PagingSource
import com.sandy.matcheap.domain.model.store.StoreItem
import com.sandy.matcheap.domain.repository.search.GetSearchStoreRepository
import javax.inject.Inject

class GetSearchStoreUseCase @Inject constructor(
    private val getSearchStoreRepository: GetSearchStoreRepository
) {

    suspend operator fun invoke(
        param: String,
        curLat: Double,
        curLng: Double
    ): PagingSource<Int, StoreItem> = getSearchStoreRepository.requestSearchStore(param, curLat, curLng)

}