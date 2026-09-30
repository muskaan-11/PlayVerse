package com.droids.playverse.data.repository

import com.droids.playverse.data.local.db.dao.AppSettingsDao
import com.droids.playverse.data.local.db.entities.AppSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository {
    val settings: Flow<AppSettingsEntity>
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setMusicEnabled(enabled: Boolean)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setHasRatedApp(rated: Boolean)
    suspend fun setAdsRemoved(removed: Boolean)
}

class SettingsRepositoryImpl(
    private val settingsDao: AppSettingsDao
) : SettingsRepository {

    override val settings: Flow<AppSettingsEntity> = settingsDao.getSettings()
        .map { it ?: AppSettingsEntity() }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        settingsDao.setSoundEnabled(enabled)
    }

    override suspend fun setMusicEnabled(enabled: Boolean) {
        settingsDao.setMusicEnabled(enabled)
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        settingsDao.setVibrationEnabled(enabled)
    }

    override suspend fun setHasRatedApp(rated: Boolean) {
        settingsDao.setHasRatedApp(rated)
    }

    override suspend fun setAdsRemoved(removed: Boolean) {
        settingsDao.setAdsRemoved(removed)
    }
}