package com.dream.homeset.core.network

import com.dream.homeset.core.model.UnsplashPhoto
import com.dream.homeset.core.model.UnsplashCollection
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApiService {

    @GET("photos")
    suspend fun getPhotos(
        @Query("client_id") clientId: String = UnsplashConfig.CLIENT_ID,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") orderBy: String = "latest"
    ): List<UnsplashPhoto>

    @GET("photos/random")
    suspend fun getRandomPhoto(
        @Query("client_id") clientId: String = UnsplashConfig.CLIENT_ID,
        @Query("featured") featured: Boolean = true
    ): UnsplashPhoto

    @GET("collections/featured")
    suspend fun getFeaturedCollections(
        @Query("client_id") clientId: String = UnsplashConfig.CLIENT_ID,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): List<UnsplashCollection>
}

