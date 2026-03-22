package com.dream.homeset.core.domain.usecase

import androidx.paging.PagingData
import com.dream.homeset.core.domain.model.Collection
import com.dream.homeset.core.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class GetCollectionsUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(): Result<List<Collection>> {
        return repository.getCollections()
    }

    fun invokeStream(pageSize: Int = 10): Flow<PagingData<Collection>> {
        return repository.getCollectionsStream(pageSize)
    }
}
