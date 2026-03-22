package com.dream.homeset.core.di

import android.content.Context
import com.dream.homeset.core.data.local.HomeSetDatabase
import com.dream.homeset.core.data.repository.PhotoRepositoryImpl
import com.dream.homeset.core.domain.repository.PhotoRepository
import com.dream.homeset.core.network.NetworkModule

object DependencyContainer {
    private var database: HomeSetDatabase? = null
    private var photoRepository: PhotoRepository? = null

    fun getPhotoRepository(context: Context): PhotoRepository {
        return photoRepository ?: synchronized(this) {
            val db = database ?: HomeSetDatabase.getDatabase(context).also { database = it }
            val repo = PhotoRepositoryImpl(NetworkModule.unsplashApi, db.favoritePhotoDao())
            photoRepository = repo
            repo
        }
    }
}
