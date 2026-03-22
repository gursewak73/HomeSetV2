package com.dream.homeset.core.model

import com.google.gson.annotations.SerializedName

data class UnsplashPhoto(
    @SerializedName("id") val id: String,
    @SerializedName("color") val color: String?,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("urls") val urls: UnsplashUrls,
    @SerializedName("user") val user: UnsplashUser?,
    @SerializedName("links") val links: UnsplashPhotoLinks
)

data class UnsplashPhotoLinks(
    @SerializedName("download_location") val downloadLocation: String
)

