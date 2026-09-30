package com.droids.playverse.data

import android.content.Context
import com.droids.playverse.data.local.db.AppDatabase
import com.droids.playverse.data.local.db.entities.AppSettingsEntity
import com.droids.playverse.data.repository.CoinRepository
import com.droids.playverse.data.repository.CoinRepositoryImpl
import com.droids.playverse.data.repository.ScoreRepository
import com.droids.playverse.data.repository.ScoreRepositoryImpl
import com.droids.playverse.data.repository.SettingsRepository
import com.droids.playverse.data.repository.SettingsRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object ServiceLocator {

    private val applicationScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Volatile
    private var database: AppDatabase? = null

    @Volatile
    private var coinRepository: CoinRepository? = null

    @Volatile
    private var scoreRepository: ScoreRepository? = null

    @Volatile
    private var settingsRepository: SettingsRepository? = null

    fun init(context: Context) {
        if (database != null) return

        synchronized(this) {
            if (database != null) return

            val db = AppDatabase.getInstance(context)
            database = db

            coinRepository = CoinRepositoryImpl(
                walletDao = db.coinWalletDao(),
                transactionDao = db.coinTransactionDao(),
                externalScope = applicationScope
            )
            scoreRepository = ScoreRepositoryImpl(db.gameStatsDao())
            settingsRepository = SettingsRepositoryImpl(db.appSettingsDao())

            applicationScope.launch {
                if (db.appSettingsDao().getSettingsOnce() == null) {
                    db.appSettingsDao().insertSettings(AppSettingsEntity())
                }
            }
        }
    }

    fun provideDatabase(): AppDatabase =
        database ?: error("ServiceLocator not initialized. Call ServiceLocator.init(context) first.")

    fun provideCoinRepository(): CoinRepository =
        coinRepository ?: error("ServiceLocator not initialized. Call ServiceLocator.init(context) first.")

    fun provideScoreRepository(): ScoreRepository =
        scoreRepository ?: error("ServiceLocator not initialized. Call ServiceLocator.init(context) first.")

    fun provideSettingsRepository(): SettingsRepository =
        settingsRepository ?: error("ServiceLocator not initialized. Call ServiceLocator.init(context) first.")
}