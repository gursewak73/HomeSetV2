package com.dream.homeset.core.domain.usecase

import androidx.paging.PagingData
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class GetPhotosStreamUseCase(
    private val repository: PhotoRepository
) {
    operator fun invoke(pageSize: Int = 30): Flow<PagingData<Photo>> {
        return repository.getPhotosStream(pageSize)
    }
}
