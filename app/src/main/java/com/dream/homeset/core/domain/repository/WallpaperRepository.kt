package com.dream.homeset.core.domain.repository

import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.model.WallpaperDestination

interface WallpaperRepository {
    suspend fun setWallpaper(photo: Photo, destination: WallpaperDestination): Result<Unit>
}
