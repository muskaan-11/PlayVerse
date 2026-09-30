package com.droids.playverse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.droids.playverse.data.local.db.entities.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {

    @Query("SELECT * FROM app_settings WHERE id = 0")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 0")
    suspend fun getSettingsOnce(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSettings(settings: AppSettingsEntity)

    @Query("UPDATE app_settings SET soundEnabled = :enabled WHERE id = 0")
    suspend fun setSoundEnabled(enabled: Boolean)

    @Query("UPDATE app_settings SET musicEnabled = :enabled WHERE id = 0")
    suspend fun setMusicEnabled(enabled: Boolean)

    @Query("UPDATE app_settings SET vibrationEnabled = :enabled WHERE id = 0")
    suspend fun setVibrationEnabled(enabled: Boolean)

    @Query("UPDATE app_settings SET hasRatedApp = :rated WHERE id = 0")
    suspend fun setHasRatedApp(rated: Boolean)

    @Query("UPDATE app_settings SET adsRemoved = :removed WHERE id = 0")
    suspend fun setAdsRemoved(removed: Boolean)
}