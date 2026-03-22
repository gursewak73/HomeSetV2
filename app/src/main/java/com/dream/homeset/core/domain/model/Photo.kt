package com.dream.homeset.core.domain.model

data class Photo(
    val id: String,
    val color: String?,
    val width: Int,
    val height: Int,
    val urls: PhotoUrls,
    val user: PhotoUser?,
    val downloadLocation: String
)

data class PhotoUrls(
    val raw: String?,
    val full: String?,
    val regular: String?,
    val small: String?,
    val thumb: String?
)

data class PhotoUser(
    val id: String,
    val name: String,
    val username: String,
    val profileHtmlUrl: String
)
