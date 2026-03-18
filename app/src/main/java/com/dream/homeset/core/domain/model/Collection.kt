package com.dream.homeset.core.domain.model

data class Collection(
    val id: String,
    val title: String,
    val description: String?,
    val totalPhotos: Int,
    val coverPhoto: Photo?
)
