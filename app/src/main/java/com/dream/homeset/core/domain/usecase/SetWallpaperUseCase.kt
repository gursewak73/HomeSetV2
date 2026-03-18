package com.dream.homeset.core.domain.usecase

import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.model.WallpaperDestination
import com.dream.homeset.core.domain.repository.WallpaperRepository

class SetWallpaperUseCase(
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(photo: Photo, destination: WallpaperDestination): Result<Unit> {
        return repository.setWallpaper(photo, destination)
    }
}
