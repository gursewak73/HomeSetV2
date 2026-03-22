package com.dream.homeset.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dream.homeset.core.data.datasource.UnsplashPagingSource
import com.dream.homeset.core.data.datasource.CollectionPagingSource
import com.dream.homeset.core.data.datasource.CollectionPhotosPagingSource
import com.dream.homeset.core.data.mapper.toDomain
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

    override fun getCollectionsStream(pageSize: Int): Flow<PagingData<com.dream.homeset.core.domain.model.Collection>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionPagingSource(api = api, perPage = pageSize) }
        ).flow
    }

    override fun getCollectionPhotosStream(collectionId: String, pageSize: Int): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionPhotosPagingSource(api = api, collectionId = collectionId, perPage = pageSize) }
        ).flow
    }

    override suspend fun getFeaturedPhoto(): Result<Photo> {
        return try {
            val response = api.getRandomPhoto(featured = true, count = 1)
            val photo = response.firstOrNull() ?: throw Exception("No photo found")
            Result.success(photo.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCollections(): Result<List<com.dream.homeset.core.domain.model.Collection>> {
        return try {
            val response = api.getCollections()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure<List<com.dream.homeset.core.domain.model.Collection>>(e)
        }
    }
}
