package com.dream.homeset.feature.gallery.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dream.homeset.core.data.repository.PhotoRepositoryImpl
import com.dream.homeset.core.data.repository.WallpaperRepositoryImpl
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.model.WallpaperDestination
import com.dream.homeset.core.domain.usecase.GetPhotosStreamUseCase
import com.dream.homeset.core.domain.usecase.SetWallpaperUseCase
import com.dream.homeset.core.network.NetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WallpaperGalleryViewModel(
    private val getPhotosStreamUseCase: GetPhotosStreamUseCase = GetPhotosStreamUseCase(
        PhotoRepositoryImpl(NetworkModule.unsplashApi)
    ),
    private val setWallpaperUseCaseFactory: (Context) -> SetWallpaperUseCase = { context ->
        SetWallpaperUseCase(WallpaperRepositoryImpl(context))
    }
) : ViewModel() {

    val photosPagingData: Flow<PagingData<Photo>> =
        getPhotosStreamUseCase().cachedIn(viewModelScope)

    private val _selectedPhoto = MutableStateFlow<Photo?>(null)
    val selectedPhoto: StateFlow<Photo?> = _selectedPhoto.asStateFlow()

    private val _previewPhotos = MutableStateFlow<List<Photo>>(emptyList())
    val previewPhotos: StateFlow<List<Photo>> = _previewPhotos.asStateFlow()

    private val _previewIndex = MutableStateFlow(0)
    val previewIndex: StateFlow<Int> = _previewIndex.asStateFlow()

    private val _isSettingWallpaper = MutableStateFlow(false)
    val isSettingWallpaper: StateFlow<Boolean> = _isSettingWallpaper.asStateFlow()

    private val _wallpaperSetSuccess = MutableStateFlow(false)
    val wallpaperSetSuccess: StateFlow<Boolean> = _wallpaperSetSuccess.asStateFlow()

    fun resetWallpaperSetSuccess() {
        _wallpaperSetSuccess.value = false
    }

    fun setPreviewData(photos: List<Photo>, index: Int) {
        if (photos.isNotEmpty() && index in photos.indices) {
            _previewPhotos.value = photos
            _previewIndex.value = index
            _selectedPhoto.value = photos[index]
        }
    }

    fun setSelectedPhoto(photo: Photo) {
        _selectedPhoto.value = photo
    }

    fun clearSelectedPhoto() {
        _selectedPhoto.value = null
        _previewPhotos.value = emptyList()
        _previewIndex.value = 0
    }

    fun setWallpaper(
        context: Context,
        photo: Photo,
        destination: WallpaperDestination
    ) {
        viewModelScope.launch {
            _isSettingWallpaper.value = true
            _wallpaperSetSuccess.value = false

            val useCase = setWallpaperUseCaseFactory(context)
            val result = useCase(photo, destination)

            _wallpaperSetSuccess.value = result.isSuccess
            _isSettingWallpaper.value = false
        }
    }
}