package com.droids.playverse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.droids.playverse.data.local.db.entities.CoinWalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinWalletDao {

    @Query("SELECT * FROM coin_wallet WHERE id = 0")
    fun getWallet(): Flow<CoinWalletEntity?>

    @Query("SELECT * FROM coin_wallet WHERE id = 0")
    suspend fun getWalletOnce(): CoinWalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: CoinWalletEntity)

    @Update
    suspend fun updateWallet(wallet: CoinWalletEntity)
}