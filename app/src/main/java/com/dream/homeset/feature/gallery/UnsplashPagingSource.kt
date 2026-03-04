package com.dream.homeset.feature.gallery

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dream.homeset.core.model.UnsplashPhoto
import com.dream.homeset.core.network.UnsplashApiService

class UnsplashPagingSource(
    private val api: UnsplashApiService,
    private val perPage: Int
) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val page = params.key ?: 1
        return try {
            val photos = api.getPhotos(page = page, perPage = perPage)
            val nextKey = if (photos.isEmpty()) null else page + 1

            LoadResult.Page(
                data = photos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}