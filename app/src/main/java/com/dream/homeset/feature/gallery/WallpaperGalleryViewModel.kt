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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

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

    private val _selectedPhoto = MutableStateFlow<UnsplashPhoto?>(null)
    val selectedPhoto: StateFlow<UnsplashPhoto?> = _selectedPhoto.asStateFlow()

    private val _previewPhotos = MutableStateFlow<List<UnsplashPhoto>>(emptyList())
    val previewPhotos: StateFlow<List<UnsplashPhoto>> = _previewPhotos.asStateFlow()

    private val _previewIndex = MutableStateFlow(0)
    val previewIndex: StateFlow<Int> = _previewIndex.asStateFlow()

    private val _isSettingWallpaper = MutableStateFlow(false)
    val isSettingWallpaper: StateFlow<Boolean> = _isSettingWallpaper.asStateFlow()

    private val _wallpaperSetSuccess = MutableStateFlow(false)
    val wallpaperSetSuccess: StateFlow<Boolean> = _wallpaperSetSuccess.asStateFlow()

    fun resetWallpaperSetSuccess() {
        _wallpaperSetSuccess.value = false
    }

    fun setPreviewData(photos: List<UnsplashPhoto>, index: Int) {
        if (photos.isNotEmpty() && index in photos.indices) {
            _previewPhotos.value = photos
            _previewIndex.value = index
            _selectedPhoto.value = photos[index]
        }
    }

    fun setSelectedPhoto(photo: UnsplashPhoto) {
        _selectedPhoto.value = photo
    }

    fun clearSelectedPhoto() {
        _selectedPhoto.value = null
        _previewPhotos.value = emptyList()
        _previewIndex.value = 0
    }

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
            _isSettingWallpaper.value = true
            _wallpaperSetSuccess.value = false
            try {
                withTimeoutOrNull(30000L) { // 30 second timeout
                    withContext(Dispatchers.IO) {
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
                            _wallpaperSetSuccess.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                // Log error if needed
                e.printStackTrace()
                _wallpaperSetSuccess.value = false
            } finally {
                _isSettingWallpaper.value = false
            }
        }
    }
}

