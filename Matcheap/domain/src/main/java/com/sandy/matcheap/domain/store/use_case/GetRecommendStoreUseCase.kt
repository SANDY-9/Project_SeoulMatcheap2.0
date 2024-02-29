package com.sandy.matcheap.domain.store.use_case

import com.sandy.matcheap.domain.store.repository.GetRecommendStoreRepository
import javax.inject.Inject

class GetRecommendStoreUseCase @Inject constructor(
    private val getRecommendStoreRepository: GetRecommendStoreRepository
) {
    suspend operator fun invoke() = getRecommendStoreRepository.getRecommendStore()
}