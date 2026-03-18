package com.dream.homeset.core.data.repository

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.model.WallpaperDestination
import com.dream.homeset.core.domain.repository.WallpaperRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class WallpaperRepositoryImpl(
    private val context: Context
) : WallpaperRepository {

    override suspend fun setWallpaper(photo: Photo, destination: WallpaperDestination): Result<Unit> {
        val url = photo.urls.full ?: photo.urls.regular ?: photo.urls.small ?: photo.urls.thumb
            ?: return Result.failure(Exception("No valid image URL found"))

        return try {
            withTimeoutOrNull(30000L) {
                withContext(Dispatchers.IO) {
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(url)
                        .allowHardware(false)
                        .build()

                    val result = loader.execute(request)
                    val bitmap = (result as? SuccessResult)?.drawable?.let { drawable ->
                        if (drawable is android.graphics.drawable.BitmapDrawable) {
                            drawable.bitmap
                        } else {
                            val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1080
                            val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1920
                            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bmp ->
                                val canvas = android.graphics.Canvas(bmp)
                                drawable.setBounds(0, 0, canvas.width, canvas.height)
                                drawable.draw(canvas)
                            }
                        }
                    }

                    if (bitmap != null) {
                        val wm = WallpaperManager.getInstance(context)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            val flags = when (destination) {
                                WallpaperDestination.HOME -> WallpaperManager.FLAG_SYSTEM
                                WallpaperDestination.LOCK -> WallpaperManager.FLAG_LOCK
                                WallpaperDestination.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                            }
                            wm.setBitmap(bitmap, null, true, flags)
                        } else {
                            wm.setBitmap(bitmap)
                        }
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Failed to decode bitmap"))
                    }
                }
            } ?: Result.failure(Exception("Wallpaper setting timed out"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
