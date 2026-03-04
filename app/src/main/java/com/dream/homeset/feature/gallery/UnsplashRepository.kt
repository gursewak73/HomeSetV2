package com.dream.homeset.feature.gallery

import com.dream.homeset.core.model.UnsplashPhoto
import com.dream.homeset.core.network.NetworkModule

class UnsplashRepository(
    private val api: com.dream.homeset.core.network.UnsplashApiService = NetworkModule.unsplashApi
) {

    suspend fun getPhotos(page: Int, perPage: Int): List<UnsplashPhoto> {
        return api.getPhotos(page = page, perPage = perPage)
    }
}

