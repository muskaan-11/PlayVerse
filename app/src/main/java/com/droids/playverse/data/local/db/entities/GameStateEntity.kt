package com.droids.playverse.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey
    val gameId: String,
    val highScore: Int = 0,
    val timesPlayed: Int = 0,
    val totalScore: Int = 0,
    // Continues used in the CURRENT run only. Reset to 0 via
    // ScoreRepository.startNewSession() whenever a fresh run begins.
    val continuesUsed: Int = 0,
    val lastPlayedTimestamp: Long = 0
)