package com.dream.homeset.feature.gallery

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dream.homeset.core.model.UnsplashPhoto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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
}

