package com.droids.playverse.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.droids.playverse.data.local.db.entities.GameStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: GameStatsEntity)

    @Query("SELECT * FROM game_stats WHERE gameId = :gameId")
    fun getStatsForGame(gameId: String): Flow<GameStatsEntity?>

    @Query("SELECT * FROM game_stats WHERE gameId = :gameId")
    suspend fun getStatsForGameOnce(gameId: String): GameStatsEntity?

    @Query("SELECT highScore FROM game_stats WHERE gameId = :gameId")
    fun highScoreFlow(gameId: String): Flow<Int?>

    @Query("SELECT * FROM game_stats")
    fun getAllStats(): Flow<List<GameStatsEntity>>
}