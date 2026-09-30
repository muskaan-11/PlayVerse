package com.droids.playverse.data.repository

import com.droids.playverse.data.local.db.dao.GameStatsDao
import com.droids.playverse.data.local.db.entities.GameStatsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ScoreRepository {
    fun highScoreFlow(gameId: String): Flow<Int>
    suspend fun submitScore(gameId: String, score: Int): Boolean

    /** Increments the continue count for the current session. */
    suspend fun recordContinueUsed(gameId: String)

    /** Number of continues used so far in the current (unfinished) session. */
    suspend fun getContinuesUsedInSession(gameId: String): Int

    /** Call when a fresh run starts (not a continue) to reset the session's continue count back to 0. */
    suspend fun startNewSession(gameId: String)
}

class ScoreRepositoryImpl(
    private val gameStatsDao: GameStatsDao
) : ScoreRepository {

    private val mutex = Mutex()

    override fun highScoreFlow(gameId: String): Flow<Int> {
        return gameStatsDao.highScoreFlow(gameId).map { it ?: 0 }
    }

    override suspend fun submitScore(gameId: String, score: Int): Boolean {
        return mutex.withLock {
            val existing = gameStatsDao.getStatsForGameOnce(gameId)
            val now = System.currentTimeMillis()
            val isNewHighScore = score > (existing?.highScore ?: 0)

            val updated = existing?.copy(
                highScore = maxOf(existing.highScore, score),
                timesPlayed = existing.timesPlayed + 1,
                totalScore = existing.totalScore + score,
                lastPlayedTimestamp = now
            ) ?: GameStatsEntity(
                gameId = gameId,
                highScore = score,
                timesPlayed = 1,
                totalScore = score,
                lastPlayedTimestamp = now
            )

            gameStatsDao.insertOrUpdateStats(updated)
            isNewHighScore
        }
    }

    override suspend fun recordContinueUsed(gameId: String) {
        mutex.withLock {
            val existing = gameStatsDao.getStatsForGameOnce(gameId)
            val now = System.currentTimeMillis()

            val updated = existing?.copy(
                continuesUsed = existing.continuesUsed + 1,
                lastPlayedTimestamp = now
            ) ?: GameStatsEntity(
                gameId = gameId,
                continuesUsed = 1,
                lastPlayedTimestamp = now
            )

            gameStatsDao.insertOrUpdateStats(updated)
        }
    }

    override suspend fun getContinuesUsedInSession(gameId: String): Int {
        return gameStatsDao.getStatsForGameOnce(gameId)?.continuesUsed ?: 0
    }

    override suspend fun startNewSession(gameId: String) {
        mutex.withLock {
            val existing = gameStatsDao.getStatsForGameOnce(gameId)
            val updated = existing?.copy(continuesUsed = 0)
                ?: GameStatsEntity(gameId = gameId, continuesUsed = 0)

            gameStatsDao.insertOrUpdateStats(updated)
        }
    }
}