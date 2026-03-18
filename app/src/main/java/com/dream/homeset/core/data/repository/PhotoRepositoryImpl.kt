package com.dream.homeset.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dream.homeset.core.data.datasource.UnsplashPagingSource
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.repository.PhotoRepository
import com.dream.homeset.core.network.UnsplashApiService
import kotlinx.coroutines.flow.Flow

class PhotoRepositoryImpl(
    private val api: UnsplashApiService
) : PhotoRepository {

    override fun getPhotosStream(pageSize: Int): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UnsplashPagingSource(api = api, perPage = pageSize) }
        ).flow
    }
}
