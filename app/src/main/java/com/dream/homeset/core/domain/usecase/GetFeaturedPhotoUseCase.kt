package com.dream.homeset.core.domain.usecase

import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.repository.PhotoRepository

class GetFeaturedPhotoUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(): Result<Photo> {
        return repository.getFeaturedPhoto()
    }
}
