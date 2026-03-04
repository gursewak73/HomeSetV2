package com.dream.homeset.feature.gallery

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dream.homeset.core.model.UnsplashPhoto
import com.dream.homeset.core.network.NetworkModule
import kotlinx.coroutines.flow.Flow

class UnsplashRepository(
    private val api: com.dream.homeset.core.network.UnsplashApiService = NetworkModule.unsplashApi
) {

    suspend fun getPhotos(page: Int, perPage: Int): List<UnsplashPhoto> {
        return api.getPhotos(page = page, perPage = perPage)
    }

    fun getPhotosStream(
        pageSize: Int = 30
    ): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UnsplashPagingSource(api = api, perPage = pageSize) }
        ).flow
    }
}

