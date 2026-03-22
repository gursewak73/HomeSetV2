package com.dream.homeset.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dream.homeset.core.data.local.dao.FavoritePhotoDao
import com.dream.homeset.core.data.local.entity.FavoritePhotoEntity

@Database(entities = [FavoritePhotoEntity::class], version = 2, exportSchema = false)
abstract class HomeSetDatabase : RoomDatabase() {
    abstract fun favoritePhotoDao(): FavoritePhotoDao

    companion object {
        @Volatile
        private var INSTANCE: HomeSetDatabase? = null

        fun getDatabase(context: Context): HomeSetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HomeSetDatabase::class.java,
                    "homeset_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
