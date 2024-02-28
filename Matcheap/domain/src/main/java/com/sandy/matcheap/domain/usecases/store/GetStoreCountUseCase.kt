package com.sandy.matcheap.domain.usecases.store

import com.sandy.matcheap.domain.repository.store.GetStoreCountRepository
import javax.inject.Inject

class GetStoreCountUseCase @Inject constructor(
    private val getStoreCountRepository: GetStoreCountRepository
) {
}