package com.dream.homeset.core.network

import com.dream.homeset.core.model.UnsplashPhoto
import com.dream.homeset.core.model.UnsplashCollection
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path
import retrofit2.http.Url
import retrofit2.Response

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
        @Query("featured") featured: Boolean = true,
        @Query("count") count: Int = 1
    ): List<UnsplashPhoto>

    @GET("collections")
    suspend fun getCollections(
        @Query("client_id") clientId: String = UnsplashConfig.CLIENT_ID,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): List<UnsplashCollection>
    
    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id") id: String,
        @Query("client_id") clientId: String = UnsplashConfig.CLIENT_ID,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<UnsplashPhoto>

    @GET
    suspend fun trackDownload(
        @Url url: String,
        @Query("client_id") clientId: String = UnsplashConfig.CLIENT_ID
    ): Response<Unit>
}

