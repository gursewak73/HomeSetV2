package com.dream.homeset.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dream.homeset.core.data.datasource.UnsplashPagingSource
import com.dream.homeset.core.data.datasource.CollectionPagingSource
import com.dream.homeset.core.data.datasource.CollectionPhotosPagingSource
import com.dream.homeset.core.data.mapper.toDomain
import com.dream.homeset.core.data.local.dao.FavoritePhotoDao
import com.dream.homeset.core.data.local.entity.toDomainModel
import com.dream.homeset.core.data.local.entity.toEntity
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.repository.PhotoRepository
import com.dream.homeset.core.network.UnsplashApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.dream.homeset.core.domain.model.Collection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepositoryImpl(
    private val api: UnsplashApiService,
    private val favoriteDao: FavoritePhotoDao
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

    override suspend fun getCollections(): Result<List<Collection>> {
        return try {
            val response = api.getCollections()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure<List<Collection>>(e)
        }
    }

    override fun getAllFavoritePhotos(): Flow<List<Photo>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun isFavorite(id: String): Flow<Boolean> {
        return favoriteDao.isFavorite(id)
    }

    override suspend fun toggleFavorite(photo: Photo) {
        withContext(Dispatchers.IO) {
            val isFav = favoriteDao.isFavorite(photo.id).first()
            if (isFav) {
                favoriteDao.deleteById(photo.id)
            } else {
                favoriteDao.insertFavorite(photo.toEntity())
            }
        }
    }

    override suspend fun trackDownload(url: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.trackDownload(url)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to track download: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
