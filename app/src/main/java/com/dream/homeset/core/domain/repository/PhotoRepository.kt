package com.dream.homeset.core.domain.repository

import androidx.paging.PagingData
import com.dream.homeset.core.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotosStream(pageSize: Int = 30): Flow<PagingData<Photo>>
}
