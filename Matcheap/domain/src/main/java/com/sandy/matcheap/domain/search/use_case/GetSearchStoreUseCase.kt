package com.sandy.matcheap.domain.search.use_case

import androidx.paging.PagingSource
import com.sandy.matcheap.domain.model.store.StoreItem
import com.sandy.matcheap.domain.search.repository.GetSearchStoreRepository
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