package com.sandy.matcheap.domain.store.use_case

import com.sandy.matcheap.domain.store.repository.GetStoreCountRepository
import javax.inject.Inject

class GetStoreCountUseCase @Inject constructor(
    private val getStoreCountRepository: GetStoreCountRepository
) {
}