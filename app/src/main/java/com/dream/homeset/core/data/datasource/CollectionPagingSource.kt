package com.dream.homeset.core.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dream.homeset.core.data.mapper.toDomain
import com.dream.homeset.core.domain.model.Collection
import com.dream.homeset.core.network.UnsplashApiService

class CollectionPagingSource(
    private val api: UnsplashApiService,
    private val perPage: Int
) : PagingSource<Int, Collection>() {

    override fun getRefreshKey(state: PagingState<Int, Collection>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {
        val page = params.key ?: 1
        return try {
            val response = api.getCollections(page = page, perPage = perPage)
            LoadResult.Page(
                data = response.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}