package com.dream.homeset.core.data.local.dao

import androidx.room.*
import com.dream.homeset.core.data.local.entity.FavoritePhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePhotoDao {
    @Query("SELECT * FROM favorite_photos ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoritePhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(photo: FavoritePhotoEntity): Long

    @Delete
    fun deleteFavorite(photo: FavoritePhotoEntity): Int

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_photos WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Query("DELETE FROM favorite_photos WHERE id = :id")
    fun deleteById(id: String): Int
}
