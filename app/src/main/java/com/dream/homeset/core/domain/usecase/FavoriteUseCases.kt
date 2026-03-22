package com.dream.homeset.core.domain.usecase

import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class GetFavoritePhotosUseCase(
    private val repository: PhotoRepository
) {
    operator fun invoke(): Flow<List<Photo>> = repository.getAllFavoritePhotos()
}

class IsFavoriteUseCase(
    private val repository: PhotoRepository
) {
    operator fun invoke(id: String): Flow<Boolean> = repository.isFavorite(id)
}

class ToggleFavoriteUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(photo: Photo) = repository.toggleFavorite(photo)
}
