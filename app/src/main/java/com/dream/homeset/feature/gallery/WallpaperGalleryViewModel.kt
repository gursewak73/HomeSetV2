package com.dream.homeset.feature.gallery

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.dream.homeset.core.model.UnsplashPhoto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

enum class WallpaperDestination {
    HOME,
    LOCK,
    BOTH
}

class WallpaperGalleryViewModel(
    private val repository: UnsplashRepository = UnsplashRepository()
) : ViewModel() {

    val photosPagingData: Flow<PagingData<UnsplashPhoto>> =
        repository.getPhotosStream().cachedIn(viewModelScope)

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            // Flow is already created; collecting happens in the UI layer via Paging Compose.
        }
    }

    fun setWallpaper(
        context: Context,
        photo: UnsplashPhoto,
        destination: WallpaperDestination
    ) {
        val url = photo.urls.full ?: photo.urls.regular ?: photo.urls.small ?: photo.urls.thumb
        if (url == null) return

        viewModelScope.launch {
            runCatching {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false)
                    .build()

                val result = loader.execute(request)
                val bitmap = (result as? SuccessResult)?.drawable
                    ?.let { drawable ->
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
                    } ?: return@runCatching

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
            }
        }
    }
}

