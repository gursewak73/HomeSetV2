package com.dream.homeset.core.data.mapper

import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.model.PhotoUrls
import com.dream.homeset.core.domain.model.PhotoUser
import com.dream.homeset.core.model.UnsplashPhoto
import com.dream.homeset.core.model.UnsplashUrls
import com.dream.homeset.core.model.UnsplashUser
import com.dream.homeset.core.model.UnsplashCollection

fun UnsplashPhoto.toDomain(): Photo {
    return Photo(
        id = id,
        color = color,
        width = width,
        height = height,
        urls = urls.toDomain(),
        user = user?.toDomain(),
        downloadLocation = links.downloadLocation
    )
}

fun UnsplashUrls.toDomain(): PhotoUrls {
    return PhotoUrls(
        raw = raw,
        full = full,
        regular = regular,
        small = small,
        thumb = thumb
    )
}

fun UnsplashUser.toDomain(): PhotoUser {
    return PhotoUser(
        id = id,
        name = name,
        username = username,
        profileHtmlUrl = links.html
    )
}

fun UnsplashCollection.toDomain(): com.dream.homeset.core.domain.model.Collection {
    return com.dream.homeset.core.domain.model.Collection(
        id = id,
        title = title,
        description = description,
        totalPhotos = totalPhotos,
        coverPhoto = coverPhoto?.toDomain()
    )
}
