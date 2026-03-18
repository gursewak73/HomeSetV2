package com.dream.homeset.core.domain.usecase

import com.dream.homeset.core.domain.model.Collection
import com.dream.homeset.core.domain.repository.PhotoRepository

class GetCollectionsUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(): Result<List<Collection>> {
        return repository.getCollections()
    }
}
