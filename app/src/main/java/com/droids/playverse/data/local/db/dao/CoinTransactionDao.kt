package com.droids.playverse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.droids.playverse.data.local.db.entities.CoinTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinTransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CoinTransactionEntity): Long

    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CoinTransactionEntity>>

    @Query("SELECT * FROM coin_transactions WHERE source = :source ORDER BY timestamp DESC")
    fun getTransactionsBySource(source: String): Flow<List<CoinTransactionEntity>>

    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<CoinTransactionEntity>>
}