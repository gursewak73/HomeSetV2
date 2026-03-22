package com.dream.homeset.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.model.PhotoUrls
import com.dream.homeset.core.domain.model.PhotoUser

@Entity(tableName = "favorite_photos")
data class FavoritePhotoEntity(
    @PrimaryKey val id: String,
    val color: String?,
    val width: Int,
    val height: Int,
    val rawUrl: String?,
    val fullUrl: String?,
    val regularUrl: String?,
    val smallUrl: String?,
    val thumbUrl: String?,
    val userId: String?,
    val userName: String?,
    val userUsername: String?,
    val createdAt: Long = System.currentTimeMillis()
)

fun FavoritePhotoEntity.toDomainModel(): Photo {
    return Photo(
        id = id,
        color = color,
        width = width,
        height = height,
        urls = PhotoUrls(
            raw = rawUrl,
            full = fullUrl,
            regular = regularUrl,
            small = smallUrl,
            thumb = thumbUrl
        ),
        user = if (userId != null && userName != null && userUsername != null) {
            PhotoUser(id = userId, name = userName, username = userUsername)
        } else null
    )
}

fun Photo.toEntity(): FavoritePhotoEntity {
    return FavoritePhotoEntity(
        id = id,
        color = color,
        width = width,
        height = height,
        rawUrl = urls.raw,
        fullUrl = urls.full,
        regularUrl = urls.regular,
        smallUrl = urls.small,
        thumbUrl = urls.thumb,
        userId = user?.id,
        userName = user?.name,
        userUsername = user?.username
    )
}
