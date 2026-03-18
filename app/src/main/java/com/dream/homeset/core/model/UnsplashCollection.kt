package com.dream.homeset.core.model

import com.google.gson.annotations.SerializedName

data class UnsplashCollection(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("total_photos") val totalPhotos: Int,
    @SerializedName("cover_photo") val coverPhoto: UnsplashPhoto?
)
