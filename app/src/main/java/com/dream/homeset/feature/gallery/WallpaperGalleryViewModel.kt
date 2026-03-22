package com.dream.homeset.feature.gallery

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dream.homeset.core.data.repository.PhotoRepositoryImpl
import com.dream.homeset.core.data.repository.WallpaperRepositoryImpl
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.model.Collection
import com.dream.homeset.core.domain.model.WallpaperDestination
import com.dream.homeset.core.domain.usecase.*
import com.dream.homeset.core.network.NetworkModule
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi

class WallpaperGalleryViewModel(
    private val getPhotosStreamUseCase: GetPhotosStreamUseCase,
    private val getFeaturedPhotoUseCase: GetFeaturedPhotoUseCase,
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val getCollectionPhotosStreamUseCase: GetCollectionPhotosStreamUseCase,
    private val getFavoritePhotosUseCase: GetFavoritePhotosUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val setWallpaperUseCaseFactory: (Context) -> SetWallpaperUseCase
) : ViewModel() {

    val photosPagingData: Flow<PagingData<Photo>> =
        getPhotosStreamUseCase().cachedIn(viewModelScope)

    val collectionsPagingData: Flow<PagingData<Collection>> =
        getCollectionsUseCase.invokeStream().cachedIn(viewModelScope)
        
    val favoritePhotos: Flow<List<Photo>> = 
        getFavoritePhotosUseCase()

    private val _selectedCollection = MutableStateFlow<Collection?>(null)
    val selectedCollection: StateFlow<Collection?> = _selectedCollection.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val collectionPhotosPagingData: Flow<PagingData<Photo>> =
        _selectedCollection.flatMapLatest { collection ->
            if (collection != null) {
                getCollectionPhotosStreamUseCase(collection.id).cachedIn(viewModelScope)
            } else {
                flowOf(PagingData.empty())
            }
        }

    private val _featuredPhoto = MutableStateFlow<Photo?>(null)
    val featuredPhoto: StateFlow<Photo?> = _featuredPhoto.asStateFlow()

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    val collections: StateFlow<List<Collection>> = _collections.asStateFlow()

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

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            getFeaturedPhotoUseCase().onSuccess {
                _featuredPhoto.value = it
            }.onFailure { e ->
                android.util.Log.e("GalleryViewModel", "Failed to load featured photo", e)
            }
            getCollectionsUseCase().onSuccess {
                _collections.value = it
            }.onFailure { e ->
                android.util.Log.e("GalleryViewModel", "Failed to load collections", e)
            }
        }
    }

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

    fun selectCollection(collection: Collection) {
        _selectedCollection.value = collection
    }

    fun clearSelectedCollection() {
        _selectedCollection.value = null
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

    fun isFavoritePhoto(id: String): Flow<Boolean> {
        return isFavoriteUseCase(id)
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            toggleFavoriteUseCase(photo)
        }
    }

    companion object {
        fun provideFactory(
            context: Context
        ): androidx.lifecycle.ViewModelProvider.Factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val repository = com.dream.homeset.core.di.DependencyContainer.getPhotoRepository(context)
                return WallpaperGalleryViewModel(
                    getPhotosStreamUseCase = GetPhotosStreamUseCase(repository),
                    getFeaturedPhotoUseCase = GetFeaturedPhotoUseCase(repository),
                    getCollectionsUseCase = GetCollectionsUseCase(repository),
                    getCollectionPhotosStreamUseCase = GetCollectionPhotosStreamUseCase(repository),
                    getFavoritePhotosUseCase = GetFavoritePhotosUseCase(repository),
                    toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
                    isFavoriteUseCase = IsFavoriteUseCase(repository),
                    setWallpaperUseCaseFactory = { ctx -> SetWallpaperUseCase(WallpaperRepositoryImpl(ctx)) }
                ) as T
            }
        }
    }
}
