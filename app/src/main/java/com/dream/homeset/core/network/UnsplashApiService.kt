package com.dream.homeset.core.network

import com.dream.homeset.core.model.UnsplashPhoto
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApiService {

    @GET("photos")
    suspend fun getPhotos(
        @Query("client_id") clientId: String = UnsplashConfig.CLIENT_ID,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<UnsplashPhoto>
}

