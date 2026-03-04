package com.dream.homeset.feature.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dream.homeset.core.model.UnsplashPhoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data class Success(val photos: List<UnsplashPhoto>) : GalleryUiState
    data class Error(val message: String) : GalleryUiState
}

class WallpaperGalleryViewModel(
    private val repository: UnsplashRepository = UnsplashRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading)
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _uiState.value = GalleryUiState.Loading
            runCatching {
                repository.getPhotos(page = 1, perPage = 30)
            }.onSuccess { photos ->
                _uiState.value = GalleryUiState.Success(photos)
            }.onFailure { throwable ->
                _uiState.value = GalleryUiState.Error(throwable.message ?: "Something went wrong")
            }
        }
    }
}

