package com.dream.homeset.core.domain.repository

import androidx.paging.PagingData
import com.dream.homeset.core.domain.model.Collection
import com.dream.homeset.core.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotosStream(pageSize: Int = 30): Flow<PagingData<Photo>>
    fun getCollectionsStream(pageSize: Int = 10): Flow<PagingData<Collection>>
    suspend fun getFeaturedPhoto(): Result<Photo>
    suspend fun getCollections(): Result<List<Collection>>
}
