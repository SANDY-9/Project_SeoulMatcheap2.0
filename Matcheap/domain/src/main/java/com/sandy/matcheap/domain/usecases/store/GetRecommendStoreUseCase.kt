package com.sandy.matcheap.domain.usecases.store

import com.sandy.matcheap.domain.repository.store.GetRecommendStoreRepository
import javax.inject.Inject

class GetRecommendStoreUseCase @Inject constructor(
    private val getRecommendStoreRepository: GetRecommendStoreRepository
) {
    suspend operator fun invoke() = getRecommendStoreRepository.getRecommendStore()
}