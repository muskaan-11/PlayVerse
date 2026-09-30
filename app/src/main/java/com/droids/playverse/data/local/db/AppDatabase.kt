package com.droids.playverse.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.droids.playverse.data.local.db.dao.AppSettingsDao
import com.droids.playverse.data.local.db.dao.CoinTransactionDao
import com.droids.playverse.data.local.db.dao.CoinWalletDao
import com.droids.playverse.data.local.db.dao.GameStatsDao
import com.droids.playverse.data.local.db.entities.AppSettingsEntity
import com.droids.playverse.data.local.db.entities.CoinTransactionEntity
import com.droids.playverse.data.local.db.entities.CoinWalletEntity
import com.droids.playverse.data.local.db.entities.GameStatsEntity

@Database(
    entities = [
        CoinWalletEntity::class,
        CoinTransactionEntity::class,
        GameStatsEntity::class,
        AppSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinWalletDao(): CoinWalletDao
    abstract fun coinTransactionDao(): CoinTransactionDao
    abstract fun gameStatsDao(): GameStatsDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "playverse_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}