package com.dream.homeset.core.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dream.homeset.core.data.mapper.toDomain
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.network.UnsplashApiService

class CollectionPhotosPagingSource(
    private val api: UnsplashApiService,
    private val collectionId: String,
    private val perPage: Int
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val page = params.key ?: 1
        return try {
            val photos = api.getCollectionPhotos(id = collectionId, page = page, perPage = perPage)
            val nextKey = if (photos.isEmpty()) null else page + 1

            LoadResult.Page(
                data = photos.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}