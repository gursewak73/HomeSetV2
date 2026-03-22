package com.dream.homeset.core.domain.usecase

import com.dream.homeset.core.domain.repository.PhotoRepository

class TrackPhotoDownloadUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(url: String): Result<Unit> {
        return repository.trackDownload(url)
    }
}